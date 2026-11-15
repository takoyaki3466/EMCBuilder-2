# EMC Builder

CompactEngine 3.1.5（BCxAddon-CE, MC 1.2.5）の **EMCビルダー** を Minecraft 1.21.1 / NeoForge 向けに移植した **Re-BuildCraft のアドオン** です。
元実装と同じく BuildCraft（Re-BuildCraft）のビルダーを継承しており、建築そのものは BC のビルダーと同じです。
違いは材料が足りないときに **共有EMC** から生成する点だけです。アイテムの EMC 値は **ProjectE** から取得します。

| 項目 | 値 |
|---|---|
| MOD ID | `emcbuilder` |
| バージョン | 1.0.0 |
| Minecraft | 1.21.1 |
| ローダー | NeoForge 21.1.266（`[21,)`） |
| Java | 21 |
| 必須前提 | Re-BuildCraft 1.1.0 以降（下記の差し替え点を含むもの）、ProjectE 1.21.1-PE1.1.0 以降 |

## 導入方法

1. NeoForge 1.21.1 をインストールする。
2. `mods` フォルダに次の 3 つを入れる。
   * Re-BuildCraft の jar
   * `ProjectE-1.21.1-PE1.1.0.jar`
   * 本 MOD の jar（`build/libs/emcbuilder-1.0.0.jar`）
3. サーバーで使う場合は、サーバーとクライアントの両方に入れる。

Re-BuildCraft には、EMC ビルダー用の差し替え点を追加した（`docs/SPEC.md` 参照）。追加前にビルドした `Re-BuildCraft-1.1.0.jar` では動かないため、Re-BuildCraft もビルドし直すこと。

ビルド: `gradlew build`。`settings.gradle` の `includeBuild('../Re-BuildCraft')` により、隣のフォルダの Re-BuildCraft を使う。
JDK 21 が必要で、Gradle を JDK 25 で起動すると失敗するため `JAVA_HOME` を JDK 21 にする。

## 使い方

1. **設計図** を Re-BuildCraft の設計台（マーカーで範囲を指定）で作る。設計図ライブラリで複製もできる。
2. **EMCビルダー**（錬成板 + Re-BuildCraft のビルダー）を置き、エンジンなどで **MJ を供給** する（BC のビルダーと同じく 1 マス 25 MJ）。
3. 左上の枠に設計図を入れると建築が始まる。建て方（向き・範囲・建設ロボット・緑マーカーの経路）は BC のビルダーと同じ。
4. 材料は 1 マスごとに次の順で使われる（元 MOD と同じ）。
   * 資材スロット（27 マス）と隣接インベントリに必要なアイテムがすべてあれば、それを使う。
   * 無ければ **共有EMC** から生成する。EMC 値の無いアイテムは EMC では作れない（資材として入れれば使える）。
   * テンプレートは BC のビルダーと同じで、EMC は使わない。
5. 共有EMC は GUI 右上の **炎のスロット** にアイテムを入れて貯める。
   * 左クリック: 手持ちのスタックをすべて変換 / 右クリック: 1 個だけ変換 / Shift+クリック: インベントリ内の同じアイテムをすべて変換
   * EMC はワールド全体で 1 つ（全ビルダー共通、上限 9,000,000,000,000,000,000）。

管理コマンド:

* `/emcbuilder emc` 共有EMCを表示
* `/emcbuilder emc set|add <量>`（OP）共有EMCを変更

## 既知の制約

* 元 MOD との違いと理由は [docs/SPEC.md](docs/SPEC.md) を参照。
* ワールドの既定ゲームモードがクリエイティブだと、BC のビルダーと同じく材料も EMC も使わない。
* 建築の細かい挙動（建てられるブロック、建築済みの判定など）は Re-BuildCraft のビルダーに従う。

## 検証状況

* Re-BuildCraft への依存へ切り替えた後は、ユーザーの指示によりビルド・起動・GameTest を **実行していない**（静的チェックのみ）。
* 以前の独自実装版の GameTest は、検証対象の再実装ごと削除した。
