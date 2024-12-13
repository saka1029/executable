# Generator

## Generatorクラス

Generatorクラスは要素を生成するコルーチンである。
GeneratorクラスはExecutableインタフェースを実装する。

### インスタンス変数

`parentContext`は呼び出し元のContextである。

`arguments`はGeneratorに渡す引数で呼び出し元のスタックから
コピーされる。

`code`はGenerator内で実行するコードを保持する。
`code`は`parentContext`から`fork()`されたContext
内で実行する。

```
final Context parentContext;
final Executable[] arguments;
final Executable code;
```

## fork()

Contextのメソッド`fork()`はContextの複製を作る。
複製はインスタンス変数globalsを共有する。

```
C0 : Context --fork()->  C1 : Context
   |                        |
   +--> stack               +--> stack
   +--> fpStack             +--> fpStack
   +--> executables         +--> executables
   +--> globals <-----------+
```

## Terminal

Terminalクラスは`Context.run()`の結果
を表すenumである。

```
Terminal run() { ... }
```

```
public enum Terminal { END, YIELD, BREAK }
```

`END`は実行すべきすべてのExecutableが完了したことを表す。

`YIELD`は`yield`の呼び出しが行われたことを表し、
スタックトップに`yield`された値が格納されている。

`BREAK`は`break`の呼び出しが行われたことを表し、
実行すべき残りのExecutableは破棄される。