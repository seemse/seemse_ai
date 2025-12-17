package org.seemse.service;

import org.seemse.common.core.exception.ServiceException;
import org.seemse.domain.bo.QueryVectorBo;
import org.seemse.domain.bo.StoreEmbeddingBo;

import java.util.List;

/**
 * 向量库管理
 * @author ageer
 */
public interface VectorStoreService {

    void storeEmbeddings(StoreEmbeddingBo storeEmbeddingBo) throws ServiceException;

    List<String> getQueryVector(QueryVectorBo queryVectorBo);

    void createSchema(String vectorModelName, String kid,String modelName);

    void removeById(String id,String modelName) throws ServiceException;

    void removeByDocId(String docId, String kid) throws ServiceException;

    void removeByFid(String fid, String kid) throws ServiceException;
}
