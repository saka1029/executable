# Generator

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