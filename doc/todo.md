# TODO

## ローカルフレーム内での再帰呼び出し

フレーム内でフレーム関数LFを定義したとする。

```
'[ ... : '[ ... : ... LF ... ] function LF ... ]
```

このローカルフレーム関数内でシンボルLFを使って再帰呼び出しを行うことはできない。
なぜならローカルフレーム関数LFの本体をパースする時点でLFは未定義だからである。
グローバルフレーム関数の場合は未定義であっても問題はない。

```
'[ ... : ... GF ... ] function GF
```

シンボルGFの名前解決は実行時に行われるからである。
LFはパース時点で名前解決を行う必要がある。
```function LF```を読み込んだ時点でシンボルLFが定義されるので、
それ以前に登場するローカルフレーム関数LF本体内のシンボルLFは未定義である。

### 解決策１：シンボルの定義を本体の前に持ってくる。

以下のような構文に変更する。

```
'[ ... : ... function LF `[ ... : ... LF ... ] ... ]
```

この場合は以下のようなマクロ的な関数定義はできなくなる。

```
'[ i - r : i '( ... ) cons function LF ... ]
```

以下のようになって、関数本体の終わりがわからなくなるためである。

```
'[ i - r : function LF i '( ... ) ... ]
```

カッコで範囲を明示する方法が考えられる。

```
'[ i - r : function LF (i '( ... )) ... ]
```

```
function ::= 'function' symbol element
```

パーサはシンボルfunctionを見つけたら次のsymbolを読み、関数として定義する。
次にelementを読むときにはsymbolは定義済となっている。
elementを読み込んだあとに、関数本体をスタック上のオフセット位置に格納するコードを生成する。