/**   
* @Title: AutoMapperInterceptor.java 
* @Package com.cyber.interceptor 
* @Description: TODO(用一句话描述该文件做什么) 
* @author cssuger@163.com   
* @date 2016年6月27日 上午10:32:55 
* @version V1.0   
*/
package com.ncvas.interceptor;

import java.sql.Connection;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import com.ncvas.interceptor.builder.SqlBuilder;
import com.ncvas.util.StringTools;
import org.apache.ibatis.builder.SqlSourceBuilder;
import org.apache.ibatis.executor.statement.StatementHandler;
import org.apache.ibatis.logging.Log;
import org.apache.ibatis.logging.LogFactory;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.ParameterMapping;
import org.apache.ibatis.mapping.SqlSource;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.plugin.Intercepts;
import org.apache.ibatis.plugin.Invocation;
import org.apache.ibatis.plugin.Plugin;
import org.apache.ibatis.plugin.Signature;
import org.apache.ibatis.reflection.DefaultReflectorFactory;
import org.apache.ibatis.reflection.MetaObject;
import org.apache.ibatis.reflection.ReflectorFactory;
import org.apache.ibatis.reflection.factory.DefaultObjectFactory;
import org.apache.ibatis.reflection.factory.ObjectFactory;
import org.apache.ibatis.reflection.wrapper.DefaultObjectWrapperFactory;
import org.apache.ibatis.reflection.wrapper.ObjectWrapperFactory;
import org.apache.ibatis.session.Configuration;


/** 
 * @ClassName: AutoMapperInterceptor 
 * @Description:mybatis 自定义拦截器，目的在于拦截udapte, insert,delet sql语句，不需要再xml里面进行配置
 * @author cssuger@163.com 
 * @date 2016年6月27日 上午10:32:55 
 *  
 */
@Intercepts({@Signature(type = StatementHandler.class, method = "prepare", args = {Connection.class})})
public class AutoMapperInterceptor implements Interceptor{

	private static final Log logger = LogFactory.getLog(AutoMapperInterceptor.class);
    private static final ObjectFactory DEFAULT_OBJECT_FACTORY = new DefaultObjectFactory();
    private static final ObjectWrapperFactory DEFAULT_OBJECT_WRAPPER_FACTORY = new DefaultObjectWrapperFactory();
    private static final ReflectorFactory DEFAULT_REFLECTOR_FACTORY = new DefaultReflectorFactory();
    

    @Override
    public Object intercept(Invocation invocation) throws Throwable {
        StatementHandler statementHandler = (StatementHandler) invocation.getTarget();
        //System.out.println(invocation.getArgs()[0]);
        MetaObject metaStatementHandler = MetaObject.forObject(statementHandler, DEFAULT_OBJECT_FACTORY,
                DEFAULT_OBJECT_WRAPPER_FACTORY,DEFAULT_REFLECTOR_FACTORY);
        // 分离代理对象链
        while (metaStatementHandler.hasGetter("h")) {
            Object object = metaStatementHandler.getValue("h");
            metaStatementHandler = MetaObject.forObject(object, DEFAULT_OBJECT_FACTORY, DEFAULT_OBJECT_WRAPPER_FACTORY,DEFAULT_REFLECTOR_FACTORY);
        }
        // 分离最后一个代理对象的目标类
        while (metaStatementHandler.hasGetter("target")) {
            Object object = metaStatementHandler.getValue("target");
            metaStatementHandler = MetaObject.forObject(object, DEFAULT_OBJECT_FACTORY, DEFAULT_OBJECT_WRAPPER_FACTORY,DEFAULT_REFLECTOR_FACTORY);
        }
        String originalSql = (String) metaStatementHandler.getValue("delegate.boundSql.sql");
        Configuration configuration = (Configuration) metaStatementHandler.getValue("delegate.configuration");
        Object parameterObject = metaStatementHandler.getValue("delegate.boundSql.parameterObject");
       
        if (StringTools.isBlank(originalSql)) {
        	 MappedStatement mappedStatement = (MappedStatement) metaStatementHandler
                     .getValue("delegate.mappedStatement");
            String newSql = "";
            // 根据ID生成相应类型的sql语句（id需剔除namespace信息）
            String id = mappedStatement.getId();
            id = id.substring(id.lastIndexOf(".") + 1);
            if (id.contains("insert")||id.contains("add") || id.contains("save")) {
                newSql = SqlBuilder.buildInsertSql(parameterObject);
            } else if (id.contains("update")) {
                newSql = SqlBuilder.buildUpdateSql(parameterObject);
            } else if ("delete".equals(id)) {
                newSql = SqlBuilder.buildDeleteSql(parameterObject);
            } 
            //else if ("select".equals(id)) {
              //  newSql = SqlBuilder.buildSelectSql(parameterObject);
           // }
            logger.debug("Auto generated sql:" + newSql);
            //
            SqlSource sqlSource = buildSqlSource(configuration, newSql, parameterObject.getClass());
            List<ParameterMapping> parameterMappings = sqlSource.getBoundSql(parameterObject).getParameterMappings();
            metaStatementHandler.setValue("delegate.boundSql.sql", sqlSource.getBoundSql(parameterObject).getSql());
            metaStatementHandler.setValue("delegate.boundSql.parameterMappings", parameterMappings);
        }
        // 调用原始statementHandler的prepare方法
        statementHandler = (StatementHandler) metaStatementHandler.getOriginalObject();
        statementHandler.prepare((Connection) invocation.getArgs()[0]);
        Object retValue = null;
        // 传递给下一个拦截器处理
        //打印sql执行时间
        long start = System.currentTimeMillis();//sql开始执行时间
        retValue = invocation.proceed();
        long end = System.currentTimeMillis();//sql结束执行时间
        long total = end - start;
        if(total > 2000){
        	String sql = (String)metaStatementHandler.getValue("delegate.boundSql.sql");
        	System.out.println("该sql:"+sql+"花费时间:"+total);
        }
        return retValue;
    }

    @Override
    public Object plugin(Object target) {
        if (target instanceof StatementHandler) {
            return Plugin.wrap(target, this);
        } else {
            return target;
        }
    }

    @Override
    public void setProperties(Properties properties) {

    }

    private SqlSource buildSqlSource(Configuration configuration, String originalSql, Class<?> parameterType) {
        SqlSourceBuilder builder = new SqlSourceBuilder(configuration);
       
        return builder.parse(originalSql, parameterType, null);
    }
    
    private SqlSource buildSqlSource(Configuration configuration, String originalSql, Class<?> parameterType,Map<String, Object> additionalParameters) {
        SqlSourceBuilder builder = new SqlSourceBuilder(configuration);
       
        return builder.parse(originalSql, parameterType, additionalParameters);
    }

}
