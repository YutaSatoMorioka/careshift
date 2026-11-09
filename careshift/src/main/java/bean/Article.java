package bean;

import java.util.List;

/** コラム記事（固定の読み物と、確認用チェックリスト）。 */
public class Article {
    private final String id;
    private final String category;
    private final String title;
    private final String summary;
    private final List<String> paragraphs;
    private final List<String> checklist;

    public Article(String id, String category, String title, String summary, List<String> paragraphs,
            List<String> checklist) {
        this.id = id;
        this.category = category;
        this.title = title;
        this.summary = summary;
        this.paragraphs = paragraphs;
        this.checklist = checklist;
    }

    public String getId() { return id; }
    public String getCategory() { return category; }
    public String getTitle() { return title; }
    public String getSummary() { return summary; }
    public List<String> getParagraphs() { return paragraphs; }
    public List<String> getChecklist() { return checklist; }
}
