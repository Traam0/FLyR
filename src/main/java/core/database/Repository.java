package core.database;

import java.util.List;
import java.util.Optional;

public interface Repository<TModel, TKey>{
    List<TModel> selectAll();
    Optional<TModel> selectWhereId(TKey id);
    List<TModel> selectWhereEq(String field, Object value);
    List<TModel> selectWhereIn(String field, Object... value);
    boolean create(TModel value);
    TModel update(TModel model);
    boolean delete(TModel model);
}