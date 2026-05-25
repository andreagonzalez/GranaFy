# Requirements Document

## Introduction

O GranaFy é um aplicativo Android de controle de gastos e investimentos pessoais, desenvolvido em Java. O aplicativo permite que usuários registrem suas entradas financeiras, gastos e investimentos, visualizem relatórios consolidados por período e gerenciem sua vida financeira de forma segura e offline. A persistência de dados é feita localmente via Room (SQLite), com segurança de autenticação baseada em SHA-256 com salt.

---

## Glossary

- **App**: O aplicativo GranaFy para Android.
- **Usuario**: Entidade que representa a pessoa autenticada no App, identificada por email e senha criptografada.
- **Gasto**: Registro de uma despesa realizada pelo Usuario, contendo data, categoria, estabelecimento, forma de pagamento, valor, parcelas e vencimento.
- **Entrada**: Registro de um recebimento financeiro do Usuario, como salário, venda ou doação.
- **Investimento**: Registro manual de um ativo financeiro do Usuario, contendo instituição, tipo e valor.
- **RelatorioFinanceiro**: Consolidação calculada de totalEntradas, totalGastos e saldoDisponivel para um período definido.
- **Estabelecimento**: Local ou empresa onde um Gasto foi realizado; pode ser pré-cadastrado ou informado manualmente.
- **TipoMacro**: Categoria principal de um Gasto (ex.: Alimentação, Transporte, Saúde).
- **Subtipo**: Subcategoria de um Gasto dentro de um TipoMacro.
- **FormaPagamento**: Interface que representa o meio de pagamento de um Gasto; implementações: Debito, Credito, Pix.
- **InvestimentoTipo**: Interface que representa a modalidade de um Investimento; implementações: Poupanca, BolsaValores.
- **FinanceService**: Serviço responsável por calcular saldo, sobras e relatórios por período.
- **SegurancaService**: Serviço responsável pela criptografia de senhas e autenticação do Usuario.
- **LoginActivity**: Tela de autenticação do App.
- **MainActivity**: Tela principal do App após autenticação, que hospeda a navegação entre módulos.
- **GastosFragment**: Módulo de listagem e cadastro de Gastos.
- **EntradasFragment**: Módulo de listagem e cadastro de Entradas.
- **InvestimentosFragment**: Módulo de listagem e cadastro de Investimentos.
- **RelatoriosFragment**: Módulo de visualização de relatórios financeiros e gráficos.
- **Room**: Biblioteca de persistência local (SQLite) utilizada pelo App.
- **SharedPreferences**: Mecanismo Android de armazenamento de preferências do Usuario, utilizado de forma criptografada.
- **MPAndroidChart**: Biblioteca utilizada para renderização de gráficos no RelatoriosFragment.
- **SHA-256**: Algoritmo de hash criptográfico utilizado pelo SegurancaService para proteger senhas.
- **Salt**: Valor aleatório concatenado à senha antes do hash para evitar ataques de dicionário.

---

## Requirements

### Requirement 1: Autenticação do Usuário

**User Story:** Como usuária do GranaFy, quero fazer login com email e senha, para que meus dados financeiros fiquem protegidos e acessíveis somente a mim.

#### Acceptance Criteria

1. THE LoginActivity SHALL exibir campos de entrada para email e senha.
2. WHEN a usuária submete o formulário de login, THE LoginActivity SHALL somente realizar a validação de formato do email (contém "@" e domínio) e comprimento mínimo da senha (6 caracteres), delegando a autenticação ao SegurancaService apenas após ambas as validações passarem.
3. WHEN o formato do email e o comprimento da senha são válidos, THE SegurancaService SHALL autenticar o Usuario comparando o hash SHA-256 com salt da senha informada com o hash armazenado para aquele email.
4. WHEN a autenticação é bem-sucedida, THE LoginActivity SHALL navegar para a MainActivity.
5. IF a senha informada não corresponde ao hash armazenado para o email, THEN THE LoginActivity SHALL exibir a mensagem "Email ou senha incorretos" sem indicar qual campo está errado.
6. IF o email informado não estiver cadastrado no banco de dados, THEN THE LoginActivity SHALL exibir a mensagem "Email ou senha incorretos" sem revelar que o email não existe.
7. IF o campo de email ou senha estiver vazio ao submeter o formulário, THEN THE LoginActivity SHALL exibir uma mensagem de validação indicando o campo obrigatório não preenchido.
8. WHEN a usuária solicita recuperação de senha com um email cadastrado, THE App SHALL enviar um email de recuperação para o endereço informado.
9. IF a usuária solicita recuperação de senha com um email não cadastrado, THEN THE LoginActivity SHALL exibir a mensagem "Se este email estiver cadastrado, você receberá as instruções de recuperação" sem confirmar ou negar o cadastro.
10. THE SegurancaService SHALL armazenar senhas exclusivamente como hash SHA-256 com salt, nunca em texto simples.
11. THE App SHALL persistir a sessão autenticada em SharedPreferences criptografado por até 30 dias, de modo que a usuária não precise fazer login novamente ao reabrir o App dentro desse período.

---

### Requirement 2: Navegação Principal

**User Story:** Como usuária do GranaFy, quero navegar entre os módulos do aplicativo de forma intuitiva, para que eu acesse rapidamente qualquer funcionalidade.

#### Acceptance Criteria

1. WHEN a autenticação é bem-sucedida, THE MainActivity SHALL exibir o logotipo do GranaFy e um menu de navegação com as opções: Gastos, Entradas, Investimentos e Relatórios.
2. WHEN a usuária seleciona um item do menu de navegação, THE MainActivity SHALL exibir o Fragment correspondente com o item selecionado visualmente destacado no menu, sem recriar a Activity.
3. WHEN a MainActivity é exibida pela primeira vez após o login, THE MainActivity SHALL sempre forçar o item "Gastos" como destacado no menu e apresentar o GastosFragment como módulo padrão, sobrepondo qualquer outro estado de seleção do menu.
4. THE App SHALL manter a posição de rolagem da lista e os dados de formulários não confirmados de cada Fragment ao alternar entre módulos, somente enquanto a sessão estiver explicitamente ativa; ao encerrar por logout, fechamento do App ou timeout de inatividade, esses dados devem ser descartados.

---

### Requirement 3: Cadastro e Listagem de Gastos

**User Story:** Como usuária do GranaFy, quero registrar e visualizar meus gastos, para que eu tenha controle detalhado das minhas despesas.

#### Acceptance Criteria

1. THE GastosFragment SHALL exibir a lista de Gastos cadastrados em ordem cronológica decrescente.
2. WHEN a usuária aciona o cadastro de novo Gasto, THE GastosFragment SHALL exibir um formulário com os campos: Data, TipoMacro, Subtipo, Estabelecimento, FormaPagamento, Valor, número de Parcelas e data de Vencimento.
3. WHEN a usuária seleciona a FormaPagamento como Credito, THE GastosFragment SHALL habilitar os campos de número de Parcelas (inteiro entre 1 e 99) e data de Vencimento.
4. WHEN a usuária seleciona a FormaPagamento como Debito ou Pix, THE GastosFragment SHALL desabilitar e ignorar os campos de Parcelas e Vencimento.
5. WHILE nenhuma FormaPagamento estiver selecionada, THE GastosFragment SHALL manter os campos de Parcelas e Vencimento desabilitados.
5. WHEN a usuária confirma o cadastro de um Gasto com os campos obrigatórios Data, TipoMacro, Estabelecimento, FormaPagamento e Valor preenchidos, THE App SHALL persistir o Gasto via Room e exibi-lo na lista.
6. IF algum dos campos obrigatórios (Data, TipoMacro, Estabelecimento, FormaPagamento, Valor) estiver vazio ao confirmar, THEN THE GastosFragment SHALL exibir uma mensagem de validação identificando o campo não preenchido.
7. WHEN a usuária aplica um filtro por período, THE GastosFragment SHALL exibir somente os Gastos cuja data esteja dentro do intervalo selecionado.
8. WHEN a usuária aplica um filtro por categoria, THE GastosFragment SHALL exibir somente os Gastos cujo TipoMacro ou Subtipo corresponda à categoria selecionada.
9. WHEN a usuária aplica um filtro por Estabelecimento, THE GastosFragment SHALL exibir somente os Gastos associados ao Estabelecimento selecionado.
10. WHEN a usuária marca um Gasto como pago, THE App SHALL atualizar o campo `pago` do Gasto para verdadeiro e persistir a alteração via Room.
11. WHEN a usuária exclui um Gasto, THE App SHALL remover o registro do banco de dados Room e atualizar a lista exibida, independentemente do estado atual da UI (inclusive se o formulário de cadastro estiver aberto).
12. IF o campo Valor do Gasto contiver um número menor ou igual a zero, THEN THE GastosFragment SHALL exibir a mensagem "O valor do gasto deve ser maior que zero".

---

### Requirement 4: Cadastro e Listagem de Entradas

**User Story:** Como usuária do GranaFy, quero registrar minhas entradas financeiras, para que eu possa acompanhar minha renda e calcular meu saldo disponível.

#### Acceptance Criteria

1. THE EntradasFragment SHALL exibir a lista de Entradas cadastradas em ordem cronológica decrescente.
2. WHEN a usuária aciona o cadastro de nova Entrada, THE EntradasFragment SHALL exibir um formulário com os campos: Data, Origem (salário, venda, doação ou outro) e Valor.
3. WHEN a usuária confirma o cadastro de uma Entrada com todos os campos obrigatórios preenchidos, THE App SHALL persistir a Entrada via Room e exibi-la na lista.
4. IF algum campo obrigatório do formulário de Entrada estiver vazio ao confirmar, THEN THE EntradasFragment SHALL exibir uma mensagem de validação indicando o campo não preenchido.
5. IF o campo Valor da Entrada contiver um número menor ou igual a zero, ou um valor não numérico, THEN THE EntradasFragment SHALL exibir a mensagem "O valor da entrada deve ser maior que zero".
6. IF o campo Valor da Entrada contiver um número maior que 999.999.999,99, THEN THE EntradasFragment SHALL exibir a mensagem "O valor informado excede o limite permitido".
7. IF o campo Data da Entrada contiver uma data futura, THEN THE EntradasFragment SHALL exibir a mensagem "A data da entrada não pode ser futura".
8. WHEN a usuária exclui uma Entrada, THE App SHALL remover o registro do banco de dados Room e atualizar a lista exibida.

---

### Requirement 5: Cadastro e Listagem de Investimentos

**User Story:** Como usuária do GranaFy, quero registrar meus investimentos manualmente, para que eu tenha uma visão consolidada do meu patrimônio.

#### Acceptance Criteria

1. THE InvestimentosFragment SHALL exibir a lista de Investimentos cadastrados em ordem cronológica decrescente.
2. WHEN a usuária aciona o cadastro de novo Investimento, THE InvestimentosFragment SHALL exibir um formulário com os campos: Data, Instituição (máximo 100 caracteres), InvestimentoTipo (Poupanca ou BolsaValores) e Valor.
3. WHEN a usuária confirma o cadastro de um Investimento com todos os campos obrigatórios preenchidos, THE App SHALL exibir o Investimento na lista imediatamente e persistir o registro via Room em background; IF a persistência falhar, THE App SHALL exibir uma mensagem de erro informando que o dado não foi salvo, mantendo o item visível na lista da sessão atual.
4. IF algum campo obrigatório do formulário de Investimento estiver vazio ao confirmar, THEN THE InvestimentosFragment SHALL exibir uma mensagem de validação indicando o campo não preenchido.
5. IF o campo Valor do Investimento contiver um número menor ou igual a zero, ou um valor não numérico, THEN THE InvestimentosFragment SHALL exibir a mensagem "O valor do investimento deve ser maior que zero".
6. IF o campo Valor do Investimento contiver um número maior que 999.999.999,99, THEN THE InvestimentosFragment SHALL exibir a mensagem "O valor informado excede o limite permitido".
7. WHEN a usuária exclui um Investimento, THE App SHALL remover o registro do banco de dados Room e atualizar a lista exibida.

---

### Requirement 6: Relatórios Financeiros

**User Story:** Como usuária do GranaFy, quero visualizar relatórios do meu fluxo financeiro por período, para que eu entenda minha situação financeira e tome decisões informadas.

#### Acceptance Criteria

1. WHEN a usuária seleciona um período, THE RelatoriosFragment SHALL exibir o RelatorioFinanceiro com totalEntradas, totalGastos e saldoDisponivel formatados com 2 casas decimais para o período selecionado.
2. THE FinanceService SHALL calcular o saldoDisponivel como a diferença entre totalEntradas e totalGastos para o período informado.
3. WHEN a usuária seleciona o período "diário", THE FinanceService SHALL calcular o RelatorioFinanceiro considerando somente registros com data igual ao dia corrente.
4. WHEN a usuária seleciona o período "semanal", THE FinanceService SHALL calcular o RelatorioFinanceiro considerando somente registros com data dentro dos últimos 7 dias.
5. WHEN a usuária seleciona o período "mensal", THE FinanceService SHALL calcular o RelatorioFinanceiro considerando somente registros com data dentro do mês corrente.
6. WHEN a usuária seleciona o período "anual", THE FinanceService SHALL calcular o RelatorioFinanceiro considerando somente registros com data dentro do ano corrente.
7. WHEN a usuária seleciona um período, THE RelatoriosFragment SHALL exibir gráfico de distribuição de Gastos por TipoMacro utilizando a biblioteca MPAndroidChart.
8. WHEN a usuária seleciona um período, THE RelatoriosFragment SHALL exibir gráfico de evolução do saldoDisponivel agrupado por dia (para períodos diário e semanal), por semana (para período mensal) e por mês (para período anual), utilizando a biblioteca MPAndroidChart.
9. IF não houver registros de Entradas ou Gastos para o período selecionado, THEN THE RelatoriosFragment SHALL exibir a mensagem "Nenhum dado disponível para o período selecionado" em vez de gráficos vazios, mantendo o seletor de período e demais elementos de UI visíveis e funcionais.
10. WHEN o RelatoriosFragment é exibido pela primeira vez, THE RelatoriosFragment SHALL apresentar o período "mensal" como seleção padrão.
11. IF o saldoDisponivel calculado for menor que zero, THEN THE RelatoriosFragment SHALL exibir o valor em destaque visual diferenciado (ex.: cor vermelha) para indicar saldo negativo.

---

### Requirement 7: Segurança e Proteção de Dados

**User Story:** Como usuária do GranaFy, quero que meus dados financeiros e credenciais estejam protegidos, para que informações sensíveis não sejam expostas.

#### Acceptance Criteria

1. WHEN um novo Usuario é cadastrado, THE SegurancaService SHALL gerar um Salt único e armazená-lo junto ao hash SHA-256 da senha no banco de dados.
2. THE SegurancaService SHALL aplicar o algoritmo SHA-256 sobre a concatenação de Salt e senha antes de armazenar ou comparar credenciais.
3. THE App SHALL armazenar credenciais e tokens de sessão exclusivamente em SharedPreferences criptografado, nunca em texto simples ou em arquivos de log.
4. THE App SHALL utilizar HTTPS para todas as comunicações de rede realizadas em integrações futuras.
5. WHERE SQLCipher estiver habilitado nas configurações de build, THE App SHALL criptografar o banco de dados Room local utilizando SQLCipher.
6. IF a sessão da usuária estiver inativa por mais de 30 minutos ou for explicitamente invalidada, THEN THE App SHALL redirecionar para a LoginActivity e limpar o token de sessão, o email e quaisquer dados de autenticação armazenados no SharedPreferences; cada gatilho de logout (timeout ou invalidação explícita) trata o redirecionamento de forma independente.

---

### Requirement 8: Persistência Local e Integridade dos Dados

**User Story:** Como usuária do GranaFy, quero que meus dados sejam salvos localmente de forma confiável, para que eu não perca informações ao fechar ou reabrir o aplicativo.

#### Acceptance Criteria

1. THE App SHALL persistir todos os campos de cada registro de Gasto (data, tipoMacro, subtipo, estabelecimento, formaPagamento, valor, parcelas, vencimento, pago), Entrada (data, origem, valor) e Investimento (data, instituicao, tipo, valor) no banco de dados Room sem perda de dados entre sessões.
2. WHEN o App é encerrado e reaberto, THE App SHALL restaurar todos os registros previamente cadastrados a partir do banco de dados Room.
3. THE App SHALL executar todas as operações de escrita no banco de dados Room em threads de background, nunca na thread principal (UI thread), permitindo que a usuária continue interagindo com o App enquanto as escritas ocorrem de forma assíncrona.
4. IF uma operação de escrita no banco de dados Room falhar, THEN THE App SHALL exibir uma mensagem de erro informando que o dado não foi salvo e registrar o erro em log interno; o log de erros pode também registrar operações bem-sucedidas para fins de diagnóstico.
5. IF a usuária tentar excluir um Estabelecimento que possua Gastos associados, THEN THE App SHALL exibir a mensagem "Este estabelecimento possui gastos vinculados e não pode ser excluído" e oferecer a opção de cancelar a operação.

---

### Requirement 9: Gerenciamento de Estabelecimentos

**User Story:** Como usuária do GranaFy, quero selecionar ou cadastrar estabelecimentos ao registrar gastos, para que eu possa filtrar e analisar despesas por local.

#### Acceptance Criteria

1. WHEN o formulário de Gasto é aberto, THE GastosFragment SHALL disponibilizar a lista de Estabelecimentos pré-cadastrados para seleção no campo Estabelecimento.
2. WHEN a usuária informa um nome de Estabelecimento não existente na lista, com no mínimo 2 e no máximo 100 caracteres, THE GastosFragment SHALL permitir o cadastro do novo Estabelecimento e associá-lo ao Gasto.
3. THE App SHALL persistir os Estabelecimentos cadastrados pela usuária via Room para reutilização em cadastros futuros.
4. WHEN a usuária digita ao menos 1 caractere no campo Estabelecimento, THE GastosFragment SHALL exibir sugestões de Estabelecimentos existentes que contenham os caracteres informados.

---

### Requirement 10: Extensibilidade para Integrações Futuras

**User Story:** Como desenvolvedora do GranaFy, quero que a arquitetura suporte integrações futuras com APIs externas, para que o aplicativo possa evoluir sem reescrita estrutural.

#### Acceptance Criteria

1. THE App SHALL implementar a camada de dados por meio de interfaces de repositório, de modo que implementações locais (Room) possam ser substituídas ou complementadas por implementações remotas sem alteração nas camadas de negócio e apresentação.
2. THE App SHALL implementar FormaPagamento como interface com implementações Debito, Credito e Pix, permitindo a adição de novas formas de pagamento sem modificação das classes existentes.
3. THE App SHALL implementar InvestimentoTipo como interface com implementações Poupanca e BolsaValores, permitindo a adição de novos tipos de investimento sem modificação das classes existentes.
4. WHERE a integração com Google Finance API estiver disponível, THE App SHALL consumir dados de cotação por meio de um cliente HTTP configurável via injeção de dependência; IF a API estiver indisponível, THEN THE App SHALL exibir os dados em cache da última sincronização bem-sucedida.
5. WHERE a integração com APIs bancárias estiver disponível, THE App SHALL importar extratos bancários e associá-los automaticamente a registros de Entrada ou Gasto existentes; IF um item do extrato não puder ser associado automaticamente, THEN THE App SHALL apresentá-lo para classificação manual pela usuária.
