# Frame

Frameは引数及びローカル変数を定義できる
ブロックである。

# スタックレイアウト

引数の数を`m`、
ローカル変数の数を`n`とする。

```
stack   fpからの相対位置
-----   ----------------
A1      fp - m
A2      fp - m + 1
...
Am      fp - 1
L1      fp
L2      fp + 1
...
Ln      fp + n - 1
```
`m = 3`、`n = 3`のとき
```
stack   fpからの相対位置
-----   ----------------
A1      fp - 3
A1      fp - 3
A2      fp - 2
L1      fp
L2      fp + 1
L3      fp + 2
```

# Frameのリンク

Frameは自分を囲む親Frameへの参照`parent`を持っている。
最上位の親は`null`を親とする。

```
function F0 [
    function F1 [ ... ]
    function F2 [ ... ]
    ...
]
```

```
     null
      ^
      |
      +-- F0:Frame
            ^
            |
            +-- F1:Frame
            |
            +-- F2:Frame
```

# Frameの構成

```
final Frame parent;
final int argumentSize;
final int returnSize;
      int localSize = 0;
final Map<Symbol, Integer> locals;
final java.util.List<Executable> executables;
```