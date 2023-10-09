/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.dromara.hodor.actuator.jobtype.kettle;

import java.util.Map;
import org.dromara.hodor.common.utils.Props;
import org.dromara.hodor.common.utils.StringUtils;
import org.dromara.hodor.common.utils.Utils;
import org.pentaho.di.base.AbstractMeta;
import org.pentaho.di.core.KettleEnvironment;
import org.pentaho.di.core.database.DatabaseMeta;
import org.pentaho.di.core.exception.KettleException;
import org.pentaho.di.core.logging.KettleLogStore;
import org.pentaho.di.core.logging.LogLevel;
import org.pentaho.di.core.plugins.PluginFolder;
import org.pentaho.di.core.plugins.StepPluginType;
import org.pentaho.di.job.Job;
import org.pentaho.di.job.JobMeta;
import org.pentaho.di.repository.Repository;
import org.pentaho.di.repository.RepositoryDirectory;
import org.pentaho.di.repository.filerep.KettleFileRepository;
import org.pentaho.di.repository.filerep.KettleFileRepositoryMeta;
import org.pentaho.di.repository.kdr.KettleDatabaseRepository;
import org.pentaho.di.repository.kdr.KettleDatabaseRepositoryMeta;
import org.pentaho.di.trans.Trans;
import org.pentaho.di.trans.TransMeta;

import static org.dromara.hodor.actuator.jobtype.kettle.KettleConstant.JOB_TYPE;
import static org.dromara.hodor.actuator.jobtype.kettle.KettleConstant.TRANS_TYPE;

/**
 * KettleProcess
 *
 * @author tomgs
 * @since 1.0
 */
public class KettleProcess {

    /**
     * 初始化环境
     *
     * @param plugins 加载插件目录
     * @throws Exception init exception
     */
    public static void init(String plugins) throws Exception {
        if (StringUtils.isNotBlank(plugins)) {
            PluginFolder pluginFolder = new PluginFolder(plugins, false, true);
            StepPluginType.getInstance().getPluginFolders().add(pluginFolder);
        }
        if (!KettleLogStore.isInitialized()) {
            KettleLogStore.init();
        }
        KettleEnvironment.init();
    }

    /**
     * 执行作业
     *
     * @param repository 资源库
     * @param path       路径
     * @param name       作业名
     * @param params     命名参数
     * @param variables  环境变量
     * @throws Exception create job exception
     */
    public static Job createKettleJob(Repository repository, String path, String name,
                                      Map<String, String> params, Map<String, String> variables) throws Exception {
        JobMeta meta = (JobMeta) getKettleMeta(repository, path, name, JOB_TYPE, params, variables);
        return new Job(repository, meta);
    }

    public static Trans createKettleTrans(Repository repository, String path, String jobName,
                                          Map<String, String> params, Map<String, String> variables) throws Exception {
        TransMeta meta = (TransMeta) getKettleMeta(repository, path, jobName, TRANS_TYPE, params, variables);
        return new Trans(meta);
    }

    public static LogLevel bulidLogLevel(String loglevel) {
        for (LogLevel level : LogLevel.values()) {
            if (level.getCode().equals(loglevel)) {
                return level;
            }
        }
        throw new IllegalArgumentException("Kettle log level " + loglevel + " is illegal");
    }

    /**
     * 获取作业元数据
     *
     * @param repository job repository
     * @param path       job path
     * @param name       job name
     * @param metaType   meta Type
     * @param params     params
     * @param variables  variables
     * @return job meta
     * @throws Exception get job meta exception
     */
    public static AbstractMeta getKettleMeta(Repository repository, String path, String name, String metaType,
                                             Map<String, String> params, Map<String, String> variables) throws Exception {
        AbstractMeta meta;
        if (repository == null && JOB_TYPE.equals(metaType)) {
            meta = new JobMeta(path, null);
        } else if (repository == null && TRANS_TYPE.equals(metaType)) {
            meta = new TransMeta(path);
        } else {
            // 加载资源库中的文件
            Utils.Assert.notNull(repository, "repository must be not null");
            meta = repository.loadJob(name, new StringDirectory(path), null, null);
        }

        if (params != null && !params.isEmpty()) {
            for (Map.Entry<String, String> item : params.entrySet()) {
                meta.setParameterValue(item.getKey(), item.getValue());
            }
        }
        if (variables != null && !variables.isEmpty()) {
            for (Map.Entry<String, String> item : variables.entrySet()) {
                meta.setVariable(item.getKey(), item.getValue());
            }
        }
        return meta;
    }

    public static Repository getRepository(String repositoryType, String repositoryPath, String jobName, Props sysProps) throws KettleException {
        if (KettleConstant.FILE_TYPE.equals(repositoryType)) {
            if (StringUtils.isEmpty(repositoryPath)) {
                return null;
            }
            KettleFileRepositoryMeta repositoryMeta = new KettleFileRepositoryMeta(null, jobName, null, repositoryPath);
            KettleFileRepository repository = new KettleFileRepository();
            repository.init(repositoryMeta);
            repository.connect(null, null);
            return repository;
        }

        if (KettleConstant.DATABASE_TYPE.equals(repositoryType)) {
            final String databaseType = sysProps.getString("database.type", "MYSQL");
            final String host = sysProps.getString("database.host");
            final String port = sysProps.getString("database.port", "3306");
            final String username = sysProps.getString("database.username");
            final String pwd = sysProps.getString("database.password");
            final String dbName = sysProps.getString("database.dbName");
            final String repoUsername = sysProps.getString("repository.username");
            final String repoPwd = sysProps.getString("repository.password");
            DatabaseMeta databaseMeta = new DatabaseMeta("kettle", databaseType, "Native", host, dbName, port, username, pwd);
            KettleDatabaseRepositoryMeta repositoryMeta = new KettleDatabaseRepositoryMeta(null, jobName, null, databaseMeta);
            KettleDatabaseRepository repository = new KettleDatabaseRepository();
            repository.init(repositoryMeta);
            repository.connect(repoUsername, repoPwd);
            return repository;
        }
        return null;
    }

    /**
     * 资源库目录类（直接指定目录，而无需递归子目录）
     */
    static class StringDirectory extends RepositoryDirectory {

        private String path;

        public StringDirectory(String path) {
            this.path = path;
        }

        @Override
        public String getPath() {
            return path;
        }
    }

}
