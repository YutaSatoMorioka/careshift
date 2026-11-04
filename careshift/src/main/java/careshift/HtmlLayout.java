package careshift;

/** 各画面で共通するHTMLヘッダーとスタイル。 */
public class HtmlLayout {
    private HtmlLayout() {}

    public static String wrap(String content) {
        return """
            <!doctype html><html lang="ja"><head>
              <meta charset="UTF-8"><meta name="viewport" content="width=device-width,initial-scale=1">
              <title>Care Shift</title><style>
              *{box-sizing:border-box}body{margin:0;background:#f4f7f6;color:#203333;font-family:Meiryo,'Yu Gothic',sans-serif;line-height:1.7}
              .bar{background:#176b61;color:white;padding:14px max(18px,calc((100% - 980px)/2));display:flex;justify-content:space-between;align-items:center}
              .bar a{color:white}.brand{font-size:22px;font-weight:bold;text-decoration:none}.bar nav{display:flex;gap:18px}
              .wrap{max-width:980px;margin:28px auto;padding:0 18px}.narrow{max-width:680px}.card{background:white;border:1px solid #dbe5e2;border-radius:12px;padding:22px;margin:18px 0;box-shadow:0 2px 8px #173a3510}
              .guide{border-left:5px solid #328578}.notice{background:#fff4d9;border:1px solid #ead39c;padding:12px 16px}.button,button{display:inline-block;background:#176b61;color:white;border:0;border-radius:7px;padding:10px 18px;text-decoration:none;font:inherit;cursor:pointer}
              .secondary{background:#e7efed;color:#174b45}h1{font-size:28px}h2{font-size:19px}.plan{font-size:23px;font-weight:bold}table{width:100%;border-collapse:collapse}th,td{text-align:left;padding:10px;border-bottom:1px solid #e5ebea}th{background:#edf4f2}.success{text-align:center;padding:42px 20px}
              @media(max-width:600px){.bar{align-items:flex-start;gap:10px;flex-direction:column}.wrap{margin:18px auto}}
              </style></head><body><header class="bar"><a class="brand" href="/">Care Shift</a><nav><a href="/">ホーム</a><a href="/?page=plan">プラン</a></nav></header>%s</body></html>
            """.formatted(content);
    }
}

