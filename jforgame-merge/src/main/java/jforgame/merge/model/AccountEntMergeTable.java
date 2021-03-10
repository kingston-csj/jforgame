package jforgame.merge.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AccountEntMergeTable implements MergeTable {

    public List<String> merge(List<Map<String, Object>> parent, List<Map<String, Object>> child) {
        Map<String, Map<String, Object>> parentMap = new HashMap();
        for (Map<String, Object> entity : parent) {
            parentMap.put((String) entity.get("id"), entity);
        }
        Map<String, Map<String, Object>> childMap = new HashMap();
        for (Map<String, Object> entity : child) {
            childMap.put((String) entity.get("id"), entity);
        }

        List<String> sqls = new ArrayList<>();
        for (Map.Entry<String, Map<String, Object>> entry : childMap.entrySet()) {
            String key = entry.getKey();
            Map<String, Object> prev = parentMap.get(key);
            if (prev == null) {
                parentMap.put(key, entry.getValue());
                sqls.add(toInsertSql(getTable(), entry.getValue()));
            } else {
                // 把child的金币加到parent
                Map<String, Object> parentEntity = parentMap.get(key);
                parentEntity.put("gold", Integer.parseInt(parentEntity.get("gold").toString()) + Integer.parseInt(entry.getValue().get("gold").toString()));
                String where = " WHERE id='" + parentEntity.get("id").toString() + "'";
                String sql = toUpdateSql(getTable(), entry.getValue()) + where;
                sqls.add(sql);
            }
        }

        return sqls;
    }

    @Override
    public String getTable() {
        return "t_account";
    }

}
