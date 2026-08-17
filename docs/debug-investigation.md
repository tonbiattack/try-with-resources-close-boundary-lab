# try-with-resourcesでclose例外が主例外になる境界を実際にデバッグする

## この記事で扱う問題

body failureとclose failureが同時に起きると、body側が主例外になり、close側はsuppressed例外になる。body側が成功すればclose例外が主例外になる。修正前はcatchで例外を作り直してsuppressed情報を失い、修正後は元のThrowableを保持する。 Spring Boot 3.3.5、Java 21、Mavenで再現する。

## 既存題材との差分

try-with-resourcesで本体例外だけを記録してしまう既存下書きとは異なり、今回はclose例外が主例外になる対照ケースも同じテストで検証する。

## 期待していた挙動と実際の挙動

| 観点 | 期待 | 修正前 | 修正後 |
|---|---|---|---|
| 利用者が観測する結果 | Java APIの契約どおりの例外・値・評価タイミング | 失敗テストの差分になる | 全テスト成功 |

## 最小再現

`ResourceService` と対応するテストを確認し、次を実行する。

```bash
git checkout <bug-commit>
mvn test
```

修正前の出力は `evidence/bug-test-output.txt` に保存している。失敗はコンパイルや依存関係ではなく、APIの意味論を誤った利用者視点のアサーションである。

## 調査：競合仮説

| 仮説 | 予測 | 結果 | 判定 |
|---|---|---|---|
| A: Spring BootのContextまたはテスト実行が原因 | 同じJava APIの最小対照でも結果が不安定になる | Contextは起動し、対照テストは契約どおり | 棄却 |
| B: Java APIのライフサイクル・合成・遅延契約を誤解している | 特定の呼び出し境界だけ結果が変わる | 失敗は対象APIの境界で再現 | 採用 |

## 原因

公式資料は次の契約を示す。 [1][1] [2][2]

> このラボの観測では、実際の値・例外・評価回数をアサーションで固定し、実装上の推測と分離した。

## 最小修正

修正は対象APIの契約に呼び出し側を合わせるだけである。不要な依存更新、リファクタリング、設計変更は加えていない。差分は修正コミットで確認できる。

## 回帰テスト

```bash
git checkout <fix-commit>
mvn clean test
```

修正後の成功出力は `evidence/fixed-test-output.txt` に保存している。元の失敗ケースと対照ケースを残し、2件のテストが成功する。

## まとめ

1. Java APIでは、値だけでなく例外の付随情報、非同期段階、評価開始条件まで契約として確認する。
2. 失敗時はSpringの設定を疑う前に、対象APIの最小実験と対照ケースを置く。
3. 最小修正は、呼び出し側の期待をAPIの実際の契約へ合わせることである。

## 参考資料

[1]: https://docs.oracle.com/javase/tutorial/essential/exceptions/tryResourceClose.html "Java公式資料"

[2]: https://docs.oracle.com/javase/8/docs/api/java/lang/Throwable.html "Java公式Javadoc"
