# ⚓ Batalha Naval — Refatoração

Refatoração de um monolito Java em uma arquitetura orientada a objetos com responsabilidades bem definidas.

## 🎯 Sobre o projeto

Este projeto é a refatoração do `Main.java` monolítico original (~450 linhas, tudo em uma classe) para uma estrutura com **9 classes** separadas por responsabilidade, usando os princípios SOLID como guia.

## 🗂️ Estrutura de pacotes

```
src/main/java/batalhanaval/
├── Main.java                       # Ponto de entrada
├── Game.java                       # Orquestrador do fluxo de jogo
│
├── model/
│   ├── Board.java                  # Tabuleiro 10×10 (estado das células)
│   ├── Ship.java                   # Navio com nome, tamanho e HP
│   ├── Fleet.java                  # Frota: posicionamento + recebe tiros
│   └── ShotResult.java             # Enum: MISS / HIT / SUNK
│
├── engine/
│   ├── CoordParser.java            # Converte "A1" ↔ [row, col]
│   └── GameLog.java                # Registra e exibe eventos
│
├── cpu/
│   ├── CpuStrategy.java            # Interface da IA
│   └── HuntTargetStrategy.java     # Implementação Hunt-and-Target
│
└── ui/
    └── BoardPrinter.java           # Renderiza tabuleiros no terminal
```

## 🔄 Mapa de refatoração

| Trecho no monolito | Classe na refatoração |
|--------------------|-----------------------|
| `char[][] ownShips / cpuShips` | `Board` |
| `int[] ownHp / cpuHp` + arrays de HP | `Ship` + `Fleet` |
| `String[] log[]` + `printLogTail` | `GameLog` |
| `printTwoBoards` + `printSingleBoard` | `BoardPrinter` |
| `parseCoord` + `prettyCoord` | `CoordParser` |
| `cpuTargets` + lógica de IA | `CpuStrategy` + `HuntTargetStrategy` |
| Loop do jogo em `main()` | `Game` |

## 🚀 Como executar

### Pré-requisito

JDK 17 ou superior instalado.

### Compilar e rodar (terminal, na raiz do projeto)

```bash
# Compilar todos os .java para a pasta out/
javac -d out $(find src/main/java -name "*.java")

# Executar
java -cp out batalhanaval.Main
```

### Rodar os testes

```bash
# Compilar tudo (main + test)
javac -d out $(find src -name "*.java")

# Executar os testes
java -cp out batalhanaval.BatalhaNavalTest
```

## 🎮 Como jogar

1. Ao iniciar, informe uma **seed** (número inteiro) para partidas reproduzíveis, ou deixe em branco para aleatório.
2. Escolha posicionar os navios **manualmente** ou de forma **automática**.
3. Durante o jogo, em cada turno você pode:
   - `1` — Atirar numa coordenada (ex: `B7`)
   - `2` — Ver os últimos 10 eventos do log
   - `3` — Ver seu tabuleiro completo
4. Vence quem afundar todos os navios do adversário.

### Frota clássica

| Navio | Tamanho |
|-------|---------|
| Porta-aviões | 5 |
| Encouraçado | 4 |
| Cruzador | 3 |
| Submarino | 3 |
| Destroyer | 2 |

## 🧠 Estratégia da CPU

A CPU usa a estratégia **Hunt-and-Target**:

1. **Hunt (caça):** atira aleatoriamente, priorizando células com soma de índices par — isso reduz a média de tiros.
2. **Target (destruição):** ao acertar, enfileira as quatro células vizinhas para tentar a seguir.
3. Ao afundar um navio, descarta a fila de alvos (60% de chance) e volta ao modo caça.

## 🧩 Princípios SOLID aplicados

- **S** — Responsabilidade Única: cada classe faz uma coisa só.
- **O** — Aberto/Fechado: `CpuStrategy` permite novas IAs sem alterar `Game`.
- **D** — Inversão de Dependência: `Game` depende da interface `CpuStrategy`, não da implementação concreta.

## 📝 Licença

Projeto acadêmico — uso livre para fins de estudo.
