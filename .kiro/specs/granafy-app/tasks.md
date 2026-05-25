# Implementation Plan: GranaFy App

## Overview

Implementação do aplicativo Android GranaFy em Java, seguindo a arquitetura de três camadas (Apresentação → Negócio → Dados). As tarefas são ordenadas por dependência: estrutura do projeto e interfaces de domínio primeiro, depois camada de dados (Room), camada de negócio (Services), camada de apresentação (Activities/Fragments) e, por fim, testes de integração e instrumentados.

---

## Tasks

- [ ] 1. Configurar estrutura do projeto e dependências Gradle
  - [ ] 1.1 Criar estrutura de pacotes Java no módulo `app`
    - Criar os pacotes: `data.model`, `data.dao`, `data.repository`, `data.repository.impl`, `business`, `presentation.activity`, `presentation.fragment`, `presentation.adapter`, `util`
    - Criar arquivo `AppDatabase.java` vazio no pacote `data`
    - _Requirements: 10.1_

  - [ ] 1.2 Configurar `build.gradle` com todas as dependências necessárias
    - Adicionar Room (`room-runtime`, `room-compiler`), EncryptedSharedPreferences (`security-crypto`), MPAndroidChart, jqwik (`net.jqwik:jqwik:1.8.4`), JUnit 5, JUnit 4, Mockito (`mockito-core:5.11.0`), Espresso
    - Habilitar `BuildConfig.SQLCIPHER_ENABLED` como flag de build
    - Configurar `compileOptions` para Java 8 e `ViewBinding`
    - _Requirements: 7.5, 8.3_

- [ ] 2. Implementar interfaces de domínio e modelos de dados
  - [ ] 2.1 Implementar interface `FormaPagamento` e suas implementações
    - Criar `FormaPagamento.java` com métodos `getNome()`, `aceitaParcelas()`, `aceitaVencimento()`
    - Criar `Debito.java`, `Credito.java` (`aceitaParcelas()=true`) e `Pix.java`
    - _Requirements: 3.3, 3.4, 10.2_

  - [ ]* 2.2 Escrever teste de propriedade para `FormaPagamento.aceitaParcelas()`
    - **Property 10: FormaPagamento.aceitaParcelas() é verdadeiro somente para Crédito**
    - **Validates: Requirements 3.3, 3.4**

  - [ ] 2.3 Implementar interface `InvestimentoTipo` e suas implementações
    - Criar `InvestimentoTipo.java` com métodos `getNome()`, `getDescricao()`
    - Criar `Poupanca.java` e `BolsaValores.java`
    - _Requirements: 5.2, 10.3_

  - [ ] 2.4 Criar entidades Room: `Usuario`, `Gasto`, `Entrada`, `Investimento`, `Estabelecimento`
    - Anotar cada classe com `@Entity`, `@PrimaryKey`, `@ColumnInfo`, `@ForeignKey` e `@Index` conforme o design
    - Configurar `ForeignKey.RESTRICT` em `Gasto` para `Estabelecimento`
    - _Requirements: 8.1, 9.3_

  - [ ] 2.5 Criar `Converters.java` com TypeConverters para `Date` e `BigDecimal`
    - Implementar `fromTimestamp`, `dateToTimestamp`, `fromString`, `bigDecimalToString`
    - _Requirements: 8.1_

  - [ ] 2.6 Criar modelos de relatório: `RelatorioFinanceiro`, `PontoEvolucao`, enum `Periodo`
    - Criar `RelatorioFinanceiro.java` com campos `totalEntradas`, `totalGastos`, `saldoDisponivel`, `gastosPorCategoria`, `evolucaoSaldo`
    - Criar `PontoEvolucao.java` e enum `Periodo` com valores `DIARIO`, `SEMANAL`, `MENSAL`, `ANUAL`
    - _Requirements: 6.1, 6.3, 6.4, 6.5, 6.6_

- [ ] 3. Implementar camada de dados (DAOs, repositórios e AppDatabase)
  - [ ] 3.1 Criar DAOs Room: `GastoDao`, `EntradaDao`, `InvestimentoDao`, `EstabelecimentoDao`
    - `GastoDao`: `inserir`, `atualizar`, `deletar`, `listarTodos`, `listarPorPeriodo`, `listarPorCategoria`, `listarPorEstabelecimento`
    - `EntradaDao`: `inserir`, `deletar`, `listarTodos`, `listarPorPeriodo`
    - `InvestimentoDao`: `inserir`, `deletar`, `listarTodos`
    - `EstabelecimentoDao`: `inserir`, `deletar`, `listarTodos`, `buscarPorNome`, `possuiGastosVinculados`
    - _Requirements: 3.1, 3.7, 3.8, 3.9, 4.1, 5.1, 9.4_

  - [ ] 3.2 Criar interfaces de repositório
    - Criar `GastoRepository.java`, `EntradaRepository.java`, `InvestimentoRepository.java`, `EstabelecimentoRepository.java` com os métodos definidos no design
    - _Requirements: 10.1_

  - [ ] 3.3 Implementar `AppDatabase` com singleton thread-safe e suporte a SQLCipher opcional
    - Registrar todas as entidades e `Converters.class`
    - Implementar `getInstance(Context)` com double-checked locking
    - Adicionar branch `BuildConfig.SQLCIPHER_ENABLED` para `SupportFactory`
    - _Requirements: 7.5, 8.2_

  - [ ] 3.4 Implementar classes `*RepositoryImpl` usando os DAOs
    - Criar `GastoRepositoryImpl`, `EntradaRepositoryImpl`, `InvestimentoRepositoryImpl`, `EstabelecimentoRepositoryImpl`
    - Todas as operações de escrita devem ser executadas em `ExecutorService` (background thread)
    - _Requirements: 8.3, 8.4_

  - [ ]* 3.5 Escrever testes de integração Room (banco in-memory) para os DAOs
    - Testar inserção, listagem, filtragem por período e exclusão para cada DAO
    - Testar `possuiGastosVinculados` e restrição de FK em `EstabelecimentoDao`
    - _Requirements: 8.1, 8.2, 8.5_

  - [ ]* 3.6 Escrever teste de propriedade para round-trip de persistência
    - **Property 11: Round-trip de persistência preserva todos os campos**
    - **Validates: Requirements 8.1, 8.2**

  - [ ]* 3.7 Escrever teste de propriedade para busca de estabelecimentos
    - **Property 12: Busca de estabelecimentos retorna apenas resultados que contêm o fragmento**
    - **Validates: Requirements 9.4**

- [ ] 4. Checkpoint — Verificar camada de dados
  - Garantir que todos os testes de integração Room passam. Verificar que nenhuma operação de escrita ocorre na UI thread. Perguntar ao usuário se há dúvidas antes de prosseguir.

- [ ] 5. Implementar `SegurancaService`
  - [ ] 5.1 Implementar `SegurancaService.java` com geração de salt, hash SHA-256 e autenticação
    - `gerarSalt()`: `SecureRandom` com 16 bytes em hex
    - `hashSenha(senha, salt)`: SHA-256 de `(salt + senha)` em hex
    - `autenticar(email, senhaInformada, hashArmazenado, saltArmazenado)`: comparação de hashes
    - `sessaoValida(criadaEm, ultimaAtividadeEm)`: verificar limites de 30 dias e 30 minutos
    - _Requirements: 1.3, 1.10, 1.11, 7.1, 7.2, 7.6_

  - [ ]* 5.2 Escrever teste de propriedade para hash de senha (determinismo e irreversibilidade)
    - **Property 2: Hash de senha é determinístico e irreversível**
    - **Validates: Requirements 1.3, 1.10, 7.2**

  - [ ]* 5.3 Escrever teste de propriedade para autenticação correta
    - **Property 3: Autenticação é correta para qualquer credencial**
    - **Validates: Requirements 1.3, 1.5**

  - [ ]* 5.4 Escrever teste de propriedade para unicidade de salts
    - **Property 4: Salts gerados são únicos**
    - **Validates: Requirements 7.1**

  - [ ]* 5.5 Escrever teste de propriedade para validade de sessão
    - **Property 5: Validade de sessão respeita os limites de tempo**
    - **Validates: Requirements 1.11, 7.6**

- [ ] 6. Implementar `FinanceService` e validações de negócio
  - [ ] 6.1 Implementar `FinanceService.java` com cálculo de relatório, filtragem e saldo
    - `calcularRelatorio(Periodo, List<Entrada>, List<Gasto>)`: preencher `RelatorioFinanceiro` com totais, `gastosPorCategoria` e `evolucaoSaldo`
    - `filtrarPorPeriodo(List<T>, Date inicio, Date fim)`: retornar registros com `inicio ≤ data ≤ fim`
    - `calcularSaldo(BigDecimal totalEntradas, BigDecimal totalGastos)`: retornar diferença exata (pode ser negativo)
    - _Requirements: 6.2, 6.3, 6.4, 6.5, 6.6_

  - [ ] 6.2 Implementar classe utilitária `ValidacaoUtil.java` com validações reutilizáveis
    - `validarEmail(String)`: contém "@" e domínio não vazio
    - `validarSenha(String)`: comprimento ≥ 6
    - `validarValorMonetario(BigDecimal)`: `0 < v ≤ 999.999.999,99`
    - `validarDataNaoFutura(Date)`: `data ≤ hoje`
    - `validarNomeEstabelecimento(String)`: `2 ≤ length ≤ 100`
    - _Requirements: 1.2, 1.7, 3.12, 4.5, 4.6, 4.7, 5.5, 5.6, 9.2_

  - [ ]* 6.3 Escrever teste de propriedade para validação de email e senha
    - **Property 1: Validação de formato de email e senha é universal**
    - **Validates: Requirements 1.2, 1.7**

  - [ ]* 6.4 Escrever teste de propriedade para cálculo de saldo
    - **Property 6: Cálculo de saldo é a diferença exata entre entradas e gastos**
    - **Validates: Requirements 6.2, 6.11**

  - [ ]* 6.5 Escrever teste de propriedade para filtragem por período
    - **Property 7: Filtragem por período inclui apenas registros dentro do intervalo**
    - **Validates: Requirements 6.3, 6.4, 6.5, 6.6**

  - [ ]* 6.6 Escrever teste de propriedade para validação de valor monetário
    - **Property 8: Validação de valor monetário rejeita valores inválidos e aceita válidos**
    - **Validates: Requirements 3.12, 4.5, 4.6, 5.5, 5.6**

  - [ ]* 6.7 Escrever teste de propriedade para validação de data de entrada
    - **Property 9: Validação de data rejeita datas futuras**
    - **Validates: Requirements 4.7**

  - [ ]* 6.8 Escrever teste de propriedade para validação de nome de estabelecimento
    - **Property 13: Validação de nome de estabelecimento respeita os limites de comprimento**
    - **Validates: Requirements 9.2**

- [ ] 7. Checkpoint — Verificar camada de negócio
  - Garantir que todos os testes unitários e de propriedade do `SegurancaService`, `FinanceService` e `ValidacaoUtil` passam. Perguntar ao usuário se há dúvidas antes de prosseguir.

- [ ] 8. Implementar layouts XML e recursos
  - [ ] 8.1 Criar layouts XML das Activities
    - `activity_login.xml`: campos de email, senha, botão de login, link de recuperação de senha
    - `activity_main.xml`: `FragmentContainerView` + `BottomNavigationView` com 4 itens (Gastos, Entradas, Investimentos, Relatórios)
    - _Requirements: 1.1, 2.1_

  - [ ] 8.2 Criar layouts XML dos Fragments de listagem
    - `fragment_gastos.xml`, `fragment_entradas.xml`, `fragment_investimentos.xml`: `RecyclerView` + `FloatingActionButton`
    - `fragment_relatorios.xml`: seletor de período (RadioGroup ou ChipGroup) + `TextView` para totais + dois `LineChart`/`PieChart` do MPAndroidChart
    - _Requirements: 3.1, 4.1, 5.1, 6.1, 6.7, 6.8_

  - [ ] 8.3 Criar layouts XML dos formulários (dialogs/bottom sheets)
    - `dialog_gasto_form.xml`: campos Data, TipoMacro, Subtipo, Estabelecimento (AutoCompleteTextView), FormaPagamento, Valor, Parcelas, Vencimento
    - `dialog_entrada_form.xml`: campos Data, Origem (Spinner), Valor
    - `dialog_investimento_form.xml`: campos Data, Instituição, InvestimentoTipo (Spinner), Valor
    - _Requirements: 3.2, 3.3, 4.2, 5.2_

  - [ ] 8.4 Criar layouts XML dos itens de lista (RecyclerView)
    - `item_gasto.xml`, `item_entrada.xml`, `item_investimento.xml` com campos relevantes e ações de exclusão/marcar pago
    - _Requirements: 3.1, 3.10, 3.11, 4.1, 4.8, 5.1, 5.7_

- [ ] 9. Implementar `LoginActivity`
  - [ ] 9.1 Implementar `LoginActivity.java` com validação de campos e autenticação
    - Usar `ValidacaoUtil` para validar email e senha antes de chamar `SegurancaService`
    - Exibir mensagem genérica "Email ou senha incorretos" para credenciais inválidas (não revelar qual campo)
    - Exibir mensagem de campo obrigatório quando email ou senha estiverem vazios
    - _Requirements: 1.1, 1.2, 1.5, 1.6, 1.7_

  - [ ] 9.2 Implementar persistência de sessão em `EncryptedSharedPreferences`
    - Salvar `session_email`, `session_expires` (30 dias) e `session_last_activity` após login bem-sucedido
    - Verificar sessão existente ao iniciar `LoginActivity` e navegar para `MainActivity` se válida
    - _Requirements: 1.11, 7.3_

  - [ ] 9.3 Implementar fluxo de recuperação de senha
    - Exibir diálogo de recuperação com campo de email
    - Mostrar mensagem "Se este email estiver cadastrado, você receberá as instruções de recuperação" independentemente do resultado
    - _Requirements: 1.8, 1.9_

- [ ] 10. Implementar `MainActivity` e navegação
  - [ ] 10.1 Implementar `MainActivity.java` com `BottomNavigationView` e gerenciamento de fragments
    - Usar `FragmentTransaction.hide/show` para alternar entre fragments sem recriar
    - Exibir `GastosFragment` como padrão com item "Gastos" destacado
    - _Requirements: 2.1, 2.2, 2.3, 2.4_

  - [ ] 10.2 Implementar timer de inatividade e timeout de sessão
    - Sobrescrever `onUserInteraction()` para reiniciar `Handler` com delay de 30 minutos
    - Ao expirar: limpar `EncryptedSharedPreferences` e redirecionar para `LoginActivity`
    - _Requirements: 7.6_

- [ ] 11. Implementar `GastosFragment` e `GastoAdapter`
  - [ ] 11.1 Implementar `GastosFragment.java` com listagem e filtros
    - Exibir lista em ordem cronológica decrescente via `GastoAdapter` + `RecyclerView`
    - Implementar filtros por período, categoria e estabelecimento chamando os métodos do `GastoRepository`
    - _Requirements: 3.1, 3.7, 3.8, 3.9_

  - [ ] 11.2 Implementar `GastoFormDialog.java` com validação e persistência
    - Habilitar/desabilitar campos Parcelas e Vencimento conforme `FormaPagamento.aceitaParcelas()`
    - Validar campos obrigatórios com `ValidacaoUtil` antes de persistir via `GastoRepositoryImpl`
    - Implementar `AutoCompleteTextView` para Estabelecimento com busca via `EstabelecimentoRepository.buscarPorNome()`
    - Permitir cadastro de novo Estabelecimento quando nome não existir na lista
    - _Requirements: 3.2, 3.3, 3.4, 3.5, 3.6, 3.12, 9.1, 9.2, 9.4_

  - [ ] 11.3 Implementar ações de marcar como pago e excluir gasto
    - Atualizar campo `pago` via `GastoRepository.atualizar()` em background thread
    - Excluir via `GastoRepository.deletar()` e atualizar lista
    - _Requirements: 3.10, 3.11_

- [ ] 12. Implementar `EntradasFragment` e `EntradaAdapter`
  - [ ] 12.1 Implementar `EntradasFragment.java` com listagem
    - Exibir lista em ordem cronológica decrescente via `EntradaAdapter` + `RecyclerView`
    - _Requirements: 4.1_

  - [ ] 12.2 Implementar `EntradaFormDialog.java` com validação e persistência
    - Validar campos obrigatórios, valor monetário e data não futura com `ValidacaoUtil`
    - Persistir via `EntradaRepositoryImpl` em background thread
    - _Requirements: 4.2, 4.3, 4.4, 4.5, 4.6, 4.7_

  - [ ] 12.3 Implementar ação de excluir entrada
    - Excluir via `EntradaRepository.deletar()` e atualizar lista
    - _Requirements: 4.8_

- [ ] 13. Implementar `InvestimentosFragment` e `InvestimentoAdapter`
  - [ ] 13.1 Implementar `InvestimentosFragment.java` com listagem
    - Exibir lista em ordem cronológica decrescente via `InvestimentoAdapter` + `RecyclerView`
    - _Requirements: 5.1_

  - [ ] 13.2 Implementar `InvestimentoFormDialog.java` com validação e persistência
    - Validar campos obrigatórios e valor monetário com `ValidacaoUtil`
    - Persistir via `InvestimentoRepositoryImpl` em background thread
    - _Requirements: 5.2, 5.3, 5.4, 5.5, 5.6_

  - [ ] 13.3 Implementar ação de excluir investimento
    - Excluir via `InvestimentoRepository.deletar()` e atualizar lista
    - _Requirements: 5.7_

- [ ] 14. Implementar `RelatoriosFragment` com gráficos MPAndroidChart
  - [ ] 14.1 Implementar `RelatoriosFragment.java` com seletor de período e exibição de totais
    - Apresentar período "mensal" como padrão ao abrir o fragment
    - Chamar `FinanceService.calcularRelatorio()` ao selecionar período
    - Exibir `totalEntradas`, `totalGastos` e `saldoDisponivel` formatados com 2 casas decimais
    - Destacar `saldoDisponivel` em vermelho quando negativo
    - Exibir mensagem "Nenhum dado disponível para o período selecionado" quando não houver registros
    - _Requirements: 6.1, 6.2, 6.9, 6.10, 6.11_

  - [ ] 14.2 Implementar gráfico de distribuição de gastos por categoria (PieChart)
    - Usar `gastosPorCategoria` do `RelatorioFinanceiro` para alimentar `PieChart` do MPAndroidChart
    - _Requirements: 6.7_

  - [ ] 14.3 Implementar gráfico de evolução do saldo (LineChart)
    - Usar `evolucaoSaldo` do `RelatorioFinanceiro` para alimentar `LineChart` do MPAndroidChart
    - Agrupar por dia (DIARIO/SEMANAL), semana (MENSAL) ou mês (ANUAL)
    - _Requirements: 6.8_

- [ ] 15. Checkpoint — Verificar camada de apresentação
  - Garantir que todos os fragments exibem dados corretamente, formulários validam campos e operações de escrita ocorrem em background. Perguntar ao usuário se há dúvidas antes de prosseguir.

- [ ] 16. Implementar testes instrumentados Espresso
  - [ ]* 16.1 Escrever testes Espresso para fluxo de login
    - Testar login com credenciais válidas → navega para `MainActivity`
    - Testar login com credenciais inválidas → exibe mensagem genérica
    - Testar campos vazios → exibe mensagem de campo obrigatório
    - _Requirements: 1.1, 1.2, 1.5, 1.6, 1.7_

  - [ ]* 16.2 Escrever testes Espresso para navegação entre módulos
    - Testar clique em cada item do `BottomNavigationView` e verificar fragment exibido
    - _Requirements: 2.1, 2.2, 2.3_

  - [ ]* 16.3 Escrever testes Espresso para cadastro completo de Gasto, Entrada e Investimento
    - Testar fluxo completo: abrir formulário → preencher campos → confirmar → verificar item na lista
    - _Requirements: 3.5, 4.3, 5.3_

  - [ ]* 16.4 Escrever testes Espresso para exibição de relatório
    - Pré-inserir dados via DAO e verificar exibição correta no `RelatoriosFragment`
    - _Requirements: 6.1, 6.7, 6.8_

- [ ] 17. Checkpoint final — Garantir que todos os testes passam
  - Executar todos os testes unitários, de propriedade, de integração Room e instrumentados Espresso. Corrigir falhas encontradas. Perguntar ao usuário se há dúvidas antes de concluir.

---

## Notes

- Tarefas marcadas com `*` são opcionais e podem ser puladas para um MVP mais rápido
- Cada tarefa referencia os requisitos correspondentes para rastreabilidade
- Os checkpoints garantem validação incremental a cada camada concluída
- Testes de propriedade (jqwik) validam garantias universais; testes unitários validam exemplos e casos de borda
- Todas as operações de escrita no Room devem usar `ExecutorService` ou `AsyncTask` (nunca UI thread)
- O `EncryptedSharedPreferences` requer `androidx.security:security-crypto`
- SQLCipher é opcional e controlado por `BuildConfig.SQLCIPHER_ENABLED`

## Task Dependency Graph

```json
{
  "waves": [
    { "id": 0, "tasks": ["1.1", "1.2"] },
    { "id": 1, "tasks": ["2.1", "2.3", "2.4", "2.5", "2.6"] },
    { "id": 2, "tasks": ["2.2", "3.1", "3.2"] },
    { "id": 3, "tasks": ["3.3", "3.4"] },
    { "id": 4, "tasks": ["3.5", "3.6", "3.7", "5.1", "6.2"] },
    { "id": 5, "tasks": ["5.2", "5.3", "5.4", "5.5", "6.1"] },
    { "id": 6, "tasks": ["6.3", "6.4", "6.5", "6.6", "6.7", "6.8", "8.1", "8.2", "8.3", "8.4"] },
    { "id": 7, "tasks": ["9.1", "9.2", "9.3", "10.1", "10.2", "11.1", "12.1", "13.1"] },
    { "id": 8, "tasks": ["11.2", "11.3", "12.2", "12.3", "13.2", "13.3", "14.1"] },
    { "id": 9, "tasks": ["14.2", "14.3"] },
    { "id": 10, "tasks": ["16.1", "16.2", "16.3", "16.4"] }
  ]
}
```
