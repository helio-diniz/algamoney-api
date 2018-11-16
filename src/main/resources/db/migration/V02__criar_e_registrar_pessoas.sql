CREATE TABLE pessoa(
	codigo BIGINT(20) PRIMARY KEY AUTO_INCREMENT,
	nome VARCHAR(50) NOT NULL,
	logradouro VARCHAR(30),
	numero VARCHAR(30),
	complemento VARCHAR(30),
	bairro VARCHAR(30),
	cep VARCHAR(30),
	cidade VARCHAR(30),
	estado VARCHAR(30),
	ativo BOOLEAN NOT NULL
)ENGINE InnoDB DEFAULT CHARSET=utf8;

INSERT INTO pessoa (nome, logradouro, numero, complemento, bairro, cep, cidade, estado, ativo) 
			 values("José Silva", "Rua Afonso Pena", "1234", "", "Centro", "17.123-100", "Bauru", "SP", true);
INSERT INTO pessoa (nome, logradouro, numero, complemento, bairro, cep, cidade, estado, ativo) 
			 values("Lúcia Silva", "Rua Afonso Pena", "1234", "", "Centro", "17.123-100", "Bauru", "SP", true);
INSERT INTO pessoa (nome, logradouro, numero, complemento, bairro, cep, cidade, estado, ativo) 
			 values("Marta Fernandes", "Rua Júlio Prestes", "1000", "", "Jardim Silvério", "17.192-110", "Bauru", "SP", true);			 
INSERT INTO pessoa (nome, logradouro, numero, complemento, bairro, cep, cidade, estado, ativo) 
			 values("Francisco Fernandes", "Rua Júlio Prestes", "1000", "", "Jardim Silvério", "17.192-110", "Bauru", "SP", true);	
INSERT INTO pessoa (nome, logradouro, numero, complemento, bairro, cep, cidade, estado, ativo) 
			 values("Lucas Antunes", "", "", "", "", "", "", "", true);			 
INSERT INTO pessoa (nome, logradouro, numero, complemento, bairro, cep, cidade, estado, ativo) 
			 values("Lidiane Martinez", "", "", "", "", "", "", "", true);		
INSERT INTO pessoa (nome, logradouro, numero, complemento, bairro, cep, cidade, estado, ativo) 
			 values("Marina Martinez", "", "", "", "", "", "", "", true);	
INSERT INTO pessoa (nome, logradouro, numero, complemento, bairro, cep, cidade, estado, ativo) 
			 values("Tiago Bertolucci", "", "", "", "", "", "", "", true);			 
INSERT INTO pessoa (nome, logradouro, numero, complemento, bairro, cep, cidade, estado, ativo) 
			 values("Ronaldo Bertolini", "Av. Getúlio Vargas", "9900", "Apto 600", "Altos da Cidade", "17.080.090", "Bauru", "SP", true);				 
INSERT INTO pessoa (nome, logradouro, numero, complemento, bairro, cep, cidade, estado, ativo) 
			 values("Fernanda Bertolini", "Av. Getúlio Vargas", "9900", "Apto 600", "Altos da Cidade", "17.080.090", "Bauru", "SP", true);			 