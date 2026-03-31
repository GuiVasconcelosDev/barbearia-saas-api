💈 Barbearia SaaS API
Uma API RESTful desenvolvida em Java com Spring Boot para gerenciar um sistema SaaS (Software as a Service) de barbearias. O sistema permite que múltiplas barbearias se cadastrem, gerenciem seus barbeiros, serviços, clientes e agendamentos, além de oferecer uma página exclusiva para os clientes realizarem marcações.

Um dos grandes diferenciais do projeto é a integração assíncrona com um serviço externo de mensageria para envio automático de lembretes via WhatsApp.

🚀 Tecnologias Utilizadas
Linguagem: Java 21

Framework: Spring Boot 3.x (Web, Data JPA)

Banco de Dados: MySQL

Segurança: JWT (JSON Web Tokens) com java-jwt (Auth0) e Hash de senhas com jBCrypt

Ferramentas: Maven, Lombok

Integrações: Comunicação HTTP via RestTemplate com API Node.js externa.

✨ Principais Funcionalidades
1. Sistema Multi-Tenant (SaaS)
Cadastro e Gestão: Cada barbearia possui seu próprio painel, controle de serviços, barbeiros e agenda de clientes.

Página Exclusiva (Slug): Geração de links únicos (ex: .../slug/minha-barbearia) para que os clientes de cada barbearia acessem e agendem horários.

Controle de Assinatura: Funcionalidade administrativa para bloquear ou desbloquear o acesso de barbearias (ativo = true/false), interrompendo o acesso ao painel e à página de agendamento.

2. Autenticação e Segurança
Senhas protegidas com criptografia BCrypt antes de serem salvas no banco de dados.

Geração de Tokens JWT com validade de 2 horas para controle de sessão.

Interceptor de Requisições (SegurancaInterceptor): Protege rotas privadas, garantindo que apenas usuários autenticados com o token "Bearer" acessem dados sensíveis. Rotas públicas (como login e a tela do cliente) são liberadas automaticamente.

3. Motor de Agendamentos Inteligente
Prevenção de Conflitos: O repositório realiza consultas nativas no banco de dados para garantir que um barbeiro não receba dois agendamentos no mesmo intervalo de tempo.

Cálculo Automático de Duração: O fim do agendamento é calculado automaticamente em tempo de execução, somando a duração do serviço escolhido à hora de início.

Filtro de Horários Livres: Endpoint que varre a agenda do dia e retorna apenas os "slots" de horários disponíveis para o cliente escolher.

4. Notificações Automatizadas (Cron Jobs)
O sistema conta com um Scheduler (@Scheduled) que roda a cada 15 minutos em background.

Ele busca agendamentos marcados para as próximas 2 horas e faz uma requisição POST automática para um Bot de WhatsApp (hospedado no Railway), enviando lembretes personalizados para os clientes, reduzindo a taxa de cancelamentos ou faltas (No-Show).

🗄️ Estrutura de Entidades (Modelos)
Barbearia: Entidade central do SaaS. Possui credenciais de acesso, configurações de exibição (slug, telefone, pix) e status da conta.

Barbeiro: Vinculado a uma barbearia. Realiza os atendimentos.

Servico: Catálogo de cortes/serviços com preço e duração (em minutos).

Cliente: Cadastro simplificado do usuário final (Nome e Telefone).

Agendamento: O coração do sistema. Relaciona Barbearia, Barbeiro, Serviço e Cliente, controlando o status do atendimento (Pendente, Concluído, Faltou) e se o lembrete já foi enviado.

BarbeiroServico: Tabela associativa que permite configurar durações específicas dependendo de qual barbeiro realiza qual serviço.

🛠️ Destaques Técnicos para Avaliação
Padrão MVC e Injeção de Dependências: O código está estritamente separado em Controllers, Services, Models e Repositories, facilitando a manutenção.

Tratamento de CORS: Configurações globais CrossOrigin para permitir o consumo da API por um front-end React/React Native.

Boas Práticas de Banco de Dados: Uso de BigDecimal para valores monetários e LocalDateTime para fusos e horários contínuos.

Proteção de Endpoints Externos: A comunicação com o bot de WhatsApp possui uma trava de segurança utilizando uma x-api-key no header da requisição, garantindo que apenas a API oficial possa disparar as mensagens.
