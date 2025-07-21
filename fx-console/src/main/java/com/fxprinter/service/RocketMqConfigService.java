package com.fxprinter.service;


import com.fx.app.entity.RocketMqConfig;
import com.fx.app.mapper.RocketMqConfigMapper;
import com.fxprinter.util.DbUtil;
import com.fxprinter.util.PromiseUtil;
import org.apache.ibatis.session.SqlSession;

import java.util.function.Consumer;

/**
 * RocketMq配置加载
 * @author dongyu
 * @version 1.0
 * @date 2025-07-11 15:31:11
 * @since jdk1.8
 */
public class RocketMqConfigService {

    public static RocketMqConfig getConfigById(Integer id) {
        try (SqlSession session = DbUtil.openSession()) {
            RocketMqConfigMapper mapper = session.getMapper(RocketMqConfigMapper.class);
            RocketMqConfig result = mapper.selectById(id);
            session.commit();
            return result;
        }catch (Exception e) {
            System.out.println("ERROR:" + e.getMessage());
        }
        return null;
    }

    /**
     * 根据配置id查询配置信息
     * @param id id
     * @param onSuccess 执行成功之后的方法
     * @param onError 执行失败之后方法
     */
    public static void getConfigById(Integer id, Consumer<RocketMqConfig> onSuccess, Consumer<Throwable> onError) {
        PromiseUtil.runInBackground(() -> getConfigById(id), onSuccess, onError);
    }

    /**
     * 新增数据
     */
    public static int insert(RocketMqConfig entity) {
        try (SqlSession session = DbUtil.openSession()) {
            RocketMqConfigMapper mapper = session.getMapper(RocketMqConfigMapper.class);
            int result = mapper.insert(entity);
            session.commit();
            return result;
        }
    }

    /**
     * 更新数据
     */
    public static int update(RocketMqConfig entity) {
        try (SqlSession session = DbUtil.openSession()) {
            RocketMqConfigMapper mapper = session.getMapper(RocketMqConfigMapper.class);
            int result = mapper.updateById(entity);
            session.commit();
            return result;
        }
    }

    /**
     * 删除数据
     */
    public static int delete(RocketMqConfig entity) {
        try (SqlSession session = DbUtil.openSession()) {
            RocketMqConfigMapper mapper = session.getMapper(RocketMqConfigMapper.class);
            int result = mapper.deleteById(entity);
            session.commit();
            return result;
        }
    }



}
