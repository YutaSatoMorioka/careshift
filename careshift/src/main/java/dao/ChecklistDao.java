package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.HashSet;
import java.util.Set;

/** コラムのチェックリストの状態（利用者ごと）。 */
public class ChecklistDao extends Dao {

    /** その人がチェックした項目の番号の集まり。 */
    public Set<Integer> load(long userId, String articleId) throws Exception {
        String sql = "SELECT item_index FROM checklist_states WHERE user_id = ? AND article_id = ?";
        try (Connection con = getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setLong(1, userId);
            ps.setString(2, articleId);
            try (ResultSet rs = ps.executeQuery()) {
                Set<Integer> set = new HashSet<>();
                while (rs.next()) {
                    set.add(rs.getInt(1));
                }
                return set;
            }
        }
    }

    /** チェック状態を丸ごと置き換える（1つのトランザクション）。 */
    public void save(long userId, String articleId, Set<Integer> checked) throws Exception {
        try (Connection con = getConnection()) {
            con.setAutoCommit(false);
            try {
                try (PreparedStatement ps = con.prepareStatement(
                        "DELETE FROM checklist_states WHERE user_id = ? AND article_id = ?")) {
                    ps.setLong(1, userId);
                    ps.setString(2, articleId);
                    ps.executeUpdate();
                }
                for (int index : checked) {
                    try (PreparedStatement ps = con.prepareStatement(
                            "INSERT INTO checklist_states (user_id, article_id, item_index) VALUES (?, ?, ?)")) {
                        ps.setLong(1, userId);
                        ps.setString(2, articleId);
                        ps.setInt(3, index);
                        ps.executeUpdate();
                    }
                }
                con.commit();
            } catch (Exception e) {
                con.rollback();
                throw e;
            }
        }
    }
}
