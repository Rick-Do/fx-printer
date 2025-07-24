package com.fxprinter.service;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.fx.app.entity.BaseParamConfig;
import com.fx.app.entity.ProgramServerInfo;
import com.fx.app.mapper.BaseParamConfigMapper;
import com.fx.app.mapper.ProgramServerInfoMapper;
import com.fxprinter.util.DbUtil;
import org.apache.ibatis.session.SqlSession;

import java.util.List;
import java.util.function.Function;

/**
 * @author dongyu
 * @version 1.0
 * @date 2025-07-24 14:04:43
 * @since jdk1.8
 */
public class BaseParamConfigService {

    private static <T> T sessionExecute(Function<BaseParamConfigMapper, ? extends T> function)
    {
        try (SqlSession session = DbUtil.openSession()) {
            BaseParamConfigMapper mapper = session.getMapper(BaseParamConfigMapper.class);
            T apply = function.apply(mapper);
            session.commit();
            return apply;
        }catch (Exception e) {
            System.out.println("ERROR:" + e.getMessage());
        }
        return null;
    }

    public static BaseParamConfig getConfig() {
        return sessionExecute(mapper -> {
            List<BaseParamConfig> serverInfos = mapper.selectList(new LambdaQueryWrapper<>());
            return !serverInfos.isEmpty() ? serverInfos.get(0) : null;
        });
    }

    public static Integer saveOrUpdate(BaseParamConfig config) {
       return sessionExecute(mapper -> {
            List<BaseParamConfig> configs = mapper.selectList(new LambdaQueryWrapper<>());
            if (configs.isEmpty()) {
                return mapper.insert(config);
            }
            config.setId(configs.get(0).getId());
            return mapper.updateById(config);
        });
    }

}
