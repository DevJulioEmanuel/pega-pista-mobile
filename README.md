<div align="center">

<img src="https://img.shields.io/badge/Platform-Android-3DDC84?style=for-the-badge&logo=android&logoColor=white"/>
<img src="https://img.shields.io/badge/Kotlin-7F52FF?style=for-the-badge&logo=kotlin&logoColor=white"/>
<img src="https://img.shields.io/badge/Firebase-FFCA28?style=for-the-badge&logo=firebase&logoColor=black"/>
<img src="https://img.shields.io/badge/Google_Maps-4285F4?style=for-the-badge&logo=google-maps&logoColor=white"/>
<img src="https://img.shields.io/badge/Jetpack_Compose-4285F4?style=for-the-badge&logo=jetpackcompose&logoColor=white"/>

# 🏃‍♂️ PegaPista

**Transforme cada corrida em uma experiência social e competitiva.**
</div>

---

## 📖 Sobre o projeto

O **PegaPista** nasceu de um problema real: a maioria das pessoas começa a correr com motivação, mas desiste nas primeiras semanas por falta de consistência, sensação de solidão e ausência de metas claras.

A solução combina rastreamento preciso de atividades físicas com mecânicas de gamificação e uma rede social focada em corredores, transformando o hábito de correr em uma experiência coletiva e recompensadora.

O foco principal não é apenas registrar desempenho — é manter o hábito.

---

## 🚀 Funcionalidades

### Rastreamento de atividade
- GPS em tempo real com visualização do percurso via Google Maps
- Métricas completas: distância, tempo, ritmo (pace) e calorias estimadas
- Funcionamento em background via Foreground Service
- Suporte offline com sincronização automática ao reconectar

### Rede social
- Feed de atividades dos usuários seguidos
- Publicação de corridas com fotos e descrição
- Curtidas e comentários em postagens
- Sistema de seguidores e seguindo
- Busca de atletas por nome

### Gamificação
- Sistema de **chamas** 🔥 por sequência diária de corridas
- Ranking competitivo entre amigos baseado em dias seguidos
- Recorde pessoal de sequência

### Perfil e estatísticas
- Distância total percorrida
- Tempo total de atividade
- Calorias queimadas acumuladas
- Ritmo médio histórico
- Histórico de atividades

### Notificações
- Lembretes diários configurados para incentivar a regularidade
- Notificações de interações sociais (curtidas, comentários, novos seguidores)
- Push notifications via Firebase Cloud Messaging

---

## 🛠️ Tecnologias

| Categoria | Tecnologia |
|-----------|-----------|
| Linguagem | Kotlin |
| UI | Jetpack Compose + Material Design 3 |
| Arquitetura | MVVM + Repository Pattern |
| Injeção de dependência | Koin |
| Navegação | Navigation Compose |
| Mapas | Google Maps SDK + Maps Compose |
| Autenticação | Firebase Authentication (email/senha e Google) |
| Banco remoto | Firebase Firestore |
| Armazenamento de arquivos | Firebase Storage |
| Banco local | Room (SQLite) |
| Sincronização offline | WorkManager |
| Push notifications | Firebase Cloud Messaging |
| Carregamento de imagens | Coil |
| Localização | Google Fused Location Provider |

---

## 🏗️ Arquitetura

O projeto segue o padrão **MVVM (Model-View-ViewModel)** com separação clara de responsabilidades em três camadas:

```
app/
├── data/
│   ├── local/          # Room database, DAOs, entities
│   ├── models/         # Modelos de domínio (Postagem, Usuario, Corrida...)
│   ├── repository/     # Repositórios (AuthRepository, PostRepository...)
│   └── location/       # Gerenciamento de GPS
│
├── ui/
│   ├── screens/        # Telas organizadas por funcionalidade
│   │   ├── auth/       # Login, cadastro, tela inicial
│   │   ├── corrida/    # Rastreamento, mapa, resultado
│   │   ├── home/       # Feed, home
│   │   ├── perfil/     # Perfil próprio e de outros usuários
│   │   └── social/     # Comentários, ranking, notificações
│   ├── viewmodels/     # ViewModels por funcionalidade
│   ├── components/     # Componentes reutilizáveis
│   └── theme/          # Cores, tipografia, tema Material
│
├── service/            # RunningService (Foreground Service de GPS)
├── worker/             # WorkManager workers de sincronização
├── di/                 # Módulos Koin
├── navigation/         # Grafo de navegação
└── utils/              # Utilitários (imagem, mapa, notificação, data)
```

### Fluxo de dados

```
UI (Compose) → ViewModel → Repository → [Firebase / Room]
                                      ↓
                              WorkManager (sync offline)
```

A camada local (Room) garante que corridas e postagens sejam salvas mesmo sem conexão. O WorkManager agenda a sincronização automaticamente quando a rede é restaurada.

---

## 📱 Screens principais

| Tela | Descrição |
|------|-----------|
| `InicioScreen` | Boas-vindas com opções de login e cadastro |
| `LoginScreen` | Autenticação por email/senha ou Google |
| `HomeScreen` | Dashboard com sequência, ranking de amigos e últimas atividades |
| `FeedScreen` | Feed cronológico das corridas dos usuários seguidos |
| `AtividadeBeforeScreen` | Pré-atividade com opção de iniciar rastreamento ou visualizar mapa |
| `AtividadeAfterScreen` | Rastreamento em tempo real com mapa, distância, tempo e pace |
| `RunFinishedScreen` | Publicação da corrida finalizada com foto do percurso |
| `PerfilScreen` | Perfil próprio com estatísticas e postagens |
| `PerfilUsuarioScreen` | Perfil de outro usuário com botão de seguir |
| `BuscarAmigosScreen` | Busca de atletas por nickname |
| `RankingScreen` | Ranking competitivo entre amigos |
| `ComentariosScreen` | Comentários de uma postagem |
| `NotificacoesScreen` | Central de notificações com swipe para excluir |

---


## 🔒 Segurança

- Nenhuma chave de API é versionada no repositório — todas são lidas via `local.properties` e `BuildConfig`
- Autenticação gerenciada inteiramente pelo Firebase Authentication
- Acesso ao Firestore protegido por Security Rules baseadas no UID do usuário autenticado
- Upload de imagens restrito a usuários autenticados via Firebase Storage Rules

---

<div align="center">

Desenvolvido pela equipe PegaPista — UFC

</div>
