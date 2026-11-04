package careshift;

/** HOME001 ホーム画面。 */
public class HomePage {
    private HomePage() {}

    public static String render(boolean showGuide) {
        String guide = showGuide ? """
            <section class="card guide">
              <h2>Care Shiftの使い方</h2>
              <p>家族のタスクや依頼、休息予定をここで確認できます。</p>
              <ol><li>家族グループを作成</li><li>タスクを登録して担当を決定</li><li>依頼や休息予定を共有</li></ol>
              <form method="post"><button class="secondary" name="action" value="skipGuide">ガイドをスキップする</button></form>
            </section>
            """ : "";
        return """
            <main class="wrap">
              <h1>ホーム</h1>
              %s
              <section class="card"><h2>未回答の依頼</h2><p>未回答の依頼はありません。</p><a href="#" onclick="alert('依頼一覧は担当機能の実装後に接続します');return false">依頼一覧を見る</a></section>
              <section class="card"><h2>今日のタスク</h2>
                <table><thead><tr><th>内容</th><th>期限</th><th>担当者</th><th>進捗</th></tr></thead>
                <tbody><tr><td colspan="4">今日が期限のタスクはありません</td></tr></tbody></table>
              </section>
              <section class="card"><h2>直近の休息予定</h2><p>休息予定はありません。</p></section>
              <section class="card"><h2>いまの状況に合うコラム</h2><p>おすすめコラムはまだありません。</p></section>
            </main>
            """.formatted(guide);
    }
}
