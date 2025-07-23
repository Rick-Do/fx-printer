package com.fxprinter.service;


import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
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

    public static RocketMqConfig getConfigByProgramId(Integer programId) {
        try (SqlSession session = DbUtil.openSession()) {
            RocketMqConfigMapper mapper = session.getMapper(RocketMqConfigMapper.class);
            RocketMqConfig result = mapper.selectOne(new LambdaQueryWrapper<RocketMqConfig>()
                    .eq(RocketMqConfig::getProgramId, programId)
            );
            session.commit();
            return result;
        }catch (Exception e) {
            System.out.println("ERROR:" + e.getMessage());
        }
        return null;
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

    public static int delete(Wrapper<RocketMqConfig> wrapper) {
        try (SqlSession session = DbUtil.openSession()) {
            RocketMqConfigMapper mapper = session.getMapper(RocketMqConfigMapper.class);
            int result = mapper.delete(wrapper);
            session.commit();
            return result;
        }
    }



}
