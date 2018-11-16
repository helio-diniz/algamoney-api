package com.algaworks.algamoney.api.service;

import java.io.InputStream;
import java.sql.Date;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
//import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.algaworks.algamoney.api.dto.LancamentoEstatisticaPessoa;
import com.algaworks.algamoney.api.mail.Mailer;
import com.algaworks.algamoney.api.model.Lancamento;
import com.algaworks.algamoney.api.model.Pessoa;
import com.algaworks.algamoney.api.model.Usuario;
import com.algaworks.algamoney.api.repository.LancamentoRepository;
import com.algaworks.algamoney.api.repository.PessoaRepository;
import com.algaworks.algamoney.api.repository.UsuarioRepository;
import com.algaworks.algamoney.api.service.exception.PessoaInativaOuInexistenteException;
import com.algaworks.algamoney.api.storage.S3;

import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;

@Service
public class LancamentoService {
	
	private static final String DESTINATARIO = "ROLE_PESQUISAR_LANCAMENTO"; 
	private static final Logger logger = LoggerFactory.getLogger(LancamentoService.class);
	@Autowired
	private PessoaRepository pessoaRepository;
	@Autowired 
	private LancamentoRepository lancamentoRepository;
	@Autowired
	private UsuarioRepository usuarioRepository;
	@Autowired
	private Mailer mailer;
	@Autowired
	private S3 s3;
	
	// WebCongig: classe de configuração de agendamento
	// fixedDelay: a cada 2 segundos; ao iniciar já é rodado este método; depois de rodá-lo, passa a ser rodado a cada 5s
	// @Scheduled(fixedDelay = 1000 * 2)
	// cron: executar o serviço em horário fixo (s m h dia/mes mes dia/semana)
	
	//@Scheduled(cron = "0 0 6 * * *")
	public void avisarSobreLancamentosVencidos(){
		if (logger.isDebugEnabled()){
			logger.debug("Preparando envio de e-mails de aviso de lançamentos vencidos.");
		}
		List<Lancamento> vencidos = lancamentoRepository
				.findByDataVencimentoLessThanEqualAndDataPagamentoIsNull(LocalDate.now());
		if (vencidos.isEmpty()){
			logger.info("Sem lançamento vencidos para aviso.");
			return;
		}
		logger.info("Existem {} lançamentos venciodos", vencidos.size());
		List<Usuario> destinatarios = usuarioRepository
				.findByPermissoesDescricao(DESTINATARIO);
		if (destinatarios.isEmpty()){
			logger.warn("Existem lancamentos vencido, mas o sistema não encontrou destinatários.");
			return;
		}
		mailer.avisarSobreLancamentoVencidos(vencidos, destinatarios);
		logger.info("Envio de e-mail de aviso de lancamentos vencido concluído.");
	}
	
	public byte[] relatorioPorPessoa(LocalDate dataInicio, LocalDate dataFim) throws Exception {
		List<LancamentoEstatisticaPessoa> dados = lancamentoRepository.porPessoa(dataInicio, dataFim);
		
		Map<String, Object> parametros = new HashMap<>();
		parametros.put("DT_INICIO", Date.valueOf(dataInicio));
		parametros.put("DT_FIM", Date.valueOf(dataFim));
		parametros.put("REPORT_LOCALE", new Locale("pt", "BR"));
		
		InputStream inputStream = this.getClass().getResourceAsStream(
				"/relatorios/lancamentos-por-pessoa.jasper");
		
		JasperPrint jasperPrint = JasperFillManager.fillReport(inputStream, parametros, 
				new JRBeanCollectionDataSource(dados)); 
		return JasperExportManager.exportReportToPdf(jasperPrint);
	}
	
	public Lancamento salvar(Lancamento lancamento) {
		validarPessoa(lancamento.getPessoa().getCodigo());
		if (StringUtils.hasText(lancamento.getAnexo())){
			s3.salvar(lancamento.getAnexo());
		}
		return lancamentoRepository.save(lancamento);
	}

	public Lancamento atualizar(Long codigo, Lancamento lancamento) {
		Lancamento lancamentoSalvo = buscarLancamentoPeloCodigo(codigo);
		if (!lancamento.getPessoa().equals(lancamentoSalvo.getPessoa())) {
			validarPessoa(lancamento.getPessoa().getCodigo());
		}
		if (StringUtils.isEmpty(lancamento.getAnexo())
				&& StringUtils.hasText(lancamentoSalvo.getAnexo())){
			s3.remover(lancamentoSalvo.getAnexo());
		} else if (StringUtils.hasText(lancamento.getAnexo()) 
				&& !lancamento.getAnexo().equals(lancamentoSalvo.getAnexo())){
			s3.substituir(lancamentoSalvo.getAnexo(), lancamento.getAnexo());
		}
		BeanUtils.copyProperties(lancamento, lancamentoSalvo, "codigo");
		return lancamentoRepository.save(lancamentoSalvo);
	}
	
	private Lancamento buscarLancamentoPeloCodigo(Long codigo) {
		Optional<Lancamento> lancamentoSalvo = lancamentoRepository.findById(codigo);
		if(!lancamentoSalvo.isPresent()){
			throw new EmptyResultDataAccessException(1);
		}
		return lancamentoSalvo.get();
	}

	public Pessoa validarPessoa(Long codigo) {
		
		Pessoa pessoa = null;
		if (codigo != null){
			pessoa = pessoaRepository.getOne(codigo);
		}
		if (pessoa == null || pessoa.isInativo()) {
			throw new PessoaInativaOuInexistenteException();
		}
		return pessoa;
	}


}
