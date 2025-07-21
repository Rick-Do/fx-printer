package com.fxprinter.service;


import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.fx.app.entity.ProgramServerInfo;
import com.fx.app.mapper.ProgramServerInfoMapper;
import com.fxprinter.util.DbUtil;
import org.apache.ibatis.session.SqlSession;

import java.io.Serializable;
import java.util.List;

/**
 * @author dongyu
 * @version 1.0
 * @date 2025-07-17 13:17:36
 * @since jdk1.8
 */
public class ProgramServerInfoService {

    /**
     * 根据id查询
     * @param id 主键
     * @return 程序服务信息
     */
    public static ProgramServerInfo getById(Serializable id)
    {
        try (SqlSession session = DbUtil.openSession()) {
            ProgramServerInfoMapper mapper = session.getMapper(ProgramServerInfoMapper.class);
            ProgramServerInfo result = mapper.selectById(id);
            session.commit();
            return result;
        }catch (Exception e) {
            System.out.println("ERROR:" + e.getMessage());
        }
        return null;
    }

    /**
     * 根据id查询
     * @param queryWrapper 查下信息
     * @return 程序服务信息
     */
    public static List<ProgramServerInfo> list(Wrapper<ProgramServerInfo> queryWrapper)
    {
        try (SqlSession session = DbUtil.openSession()) {
            ProgramServerInfoMapper mapper = session.getMapper(ProgramServerInfoMapper.class);
            List<ProgramServerInfo> result = mapper.selectList(queryWrapper);
            session.commit();
            return result;
        }catch (Exception e) {
            System.out.println("ERROR:" + e.getMessage());
        }
        return null;
    }

    /**
     * 根据id删除
     * @param id 主键
     * @return 删除结果
     */
    public static int deleteById(Serializable id)
    {
        try (SqlSession session = DbUtil.openSession()) {
            ProgramServerInfoMapper mapper = session.getMapper(ProgramServerInfoMapper.class);
            int result = mapper.deleteById(id);
            session.commit();
            return result;
        }catch (Exception e) {
            System.out.println("ERROR:" + e.getMessage());
        }
        return 0;
    }

    /**
     * 根据id更新
     * @param entity 实体
     * @return 更新结果
     */
    public static int updateById(ProgramServerInfo entity) {
        try (SqlSession session = DbUtil.openSession()) {
            ProgramServerInfoMapper mapper = session.getMapper(ProgramServerInfoMapper.class);
            int result = mapper.updateById(entity);
            session.commit();
            return result;
        }
    }

    /**
     * 插入
     * @param entity 插入的实体
     * @return 插入结果
     */
    public static int insert(ProgramServerInfo entity) {
        try (SqlSession session = DbUtil.openSession()) {
            ProgramServerInfoMapper mapper = session.getMapper(ProgramServerInfoMapper.class);
            int result = mapper.insert(entity);
            session.commit();
            return result;
        }
    }


}
