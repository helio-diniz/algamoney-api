package com.algaworks.algamoney.api.repository.listener;

import javax.persistence.PostLoad;

import org.springframework.util.StringUtils;

import com.algaworks.algamoney.api.SprAlgamoneyApiApplication;
import com.algaworks.algamoney.api.model.Lancamento;
import com.algaworks.algamoney.api.storage.S3;

public class LancamentoAnexoListener {

	@PostLoad // tem que também configura @EntityListeners na classe modelo de Lancamento
	public void postLoad(Lancamento lancamento){
		if (StringUtils.hasText(lancamento.getAnexo())){
			S3 s3 = SprAlgamoneyApiApplication.getBean(S3.class);
			lancamento.setUrlAnexo(s3.configurarUrl(lancamento.getAnexo()) );
		}
	}
}
