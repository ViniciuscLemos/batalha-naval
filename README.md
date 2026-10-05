# Batalha Naval

![Testes](https://github.com/ViniciuscLemos/batalha-naval/actions/workflows/testes.yml/badge.svg)

Batalha naval no terminal, jogando contra o computador.

Esse projeto começou como um `Main.java` de umas 450 linhas, tudo numa classe só. A ideia foi refatorar e separar as responsabilidades em classes (tabuleiro, navio, frota, IA, impressão...), seguindo mais ou menos o SOLID.

## Rodando

Precisa do JDK 17.

Linux/Mac:
```bash
javac -encoding UTF-8 -d out $(find src/main/java -name "*.java")
java -cp out batalhanaval.Main
```

Windows (PowerShell):
```powershell
javac -encoding UTF-8 -d out (Get-ChildItem -Recurse src\main\java -Filter *.java).FullName
java -cp out batalhanaval.Main
```

Os testes não usam framework, é só compilar a pasta `src` inteira e rodar:
```bash
javac -encoding UTF-8 -d out $(find src -name "*.java")
java -cp out batalhanaval.BatalhaNavalTest
```

## Como jogar

No começo ele pede uma seed. Se você deixar em branco, a partida é aleatória; se digitar um número, dá pra repetir a mesma partida depois. Depois você escolhe se quer posicionar os navios na mão ou deixar que ele posicione automaticamente.

Na sua vez você atira digitando uma coordenada tipo `B7`. Também dá pra ver o log da partida ou o seu tabuleiro. No fim aparecem quantos tiros cada um deu e a taxa de acerto.

A frota é a clássica: porta-aviões (5), encouraçado (4), cruzador (3), submarino (3) e destroyer (2).

## A IA

O computador usa a estratégia de "caça e destruição":
- enquanto não acerta nada, atira mais ou menos aleatório, mas preferindo as casas em xadrez, porque todo navio ocupa pelo menos 2 casas
- quando acerta, ele passa a tentar as casas vizinhas até afundar o navio

## Estrutura

```
src/main/java/batalhanaval/
  Main.java, Game.java
  model/    Board, Ship, Fleet, ShotResult
  engine/   CoordParser, GameLog
  cpu/      CpuStrategy, HuntTargetStrategy
  ui/       BoardPrinter
```

`Game` depende da interface `CpuStrategy`, então dá pra criar outra IA sem mexer no resto do jogo.
