package com.model2.mvc.service.purchase.impl;

import java.util.List;

import org.apache.ibatis.session.SqlSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

import com.model2.mvc.common.Search;
import com.model2.mvc.service.domain.Purchase;
import com.model2.mvc.service.purchase.PurchaseDao;

@Repository("purchaseDaoImpl")
public class PurchaseDaoImpl implements PurchaseDao {
	///Field
	@Autowired
	@Qualifier("sqlSessionTemplate")
	private SqlSession sqlSession;

	public void setSqlSession(SqlSession sqlSession) {
		this.sqlSession = sqlSession;
	}

	///Constructor
	public PurchaseDaoImpl() {
		System.out.println(this.getClass());
	}

	@Override
	public void addPurchase(Purchase purchase) {
		sqlSession.insert("PurchaseMapper.addPurchase", purchase);
	}

	@Override
	public Purchase getPurchase(int tranNo) {
		return sqlSession.selectOne("PurchaseMapper.getPurchase", tranNo);
	}

	@Override
	public Purchase getPurchase2(int prodNo) {
		return sqlSession.selectOne("PurchaseMapper.getPurchase2", prodNo);
	}

	@Override
	public List<Purchase> getPurchaseList(Search search, String buyerId) {
		search.setSearchKeywordInPurchase(buyerId);
		return sqlSession.selectList("PurchaseMapper.getPurchaseList", search);
	}

	@Override
	public List<Purchase> getSaleList(Search search) {
		return sqlSession.selectList("PurchaseMapper.getSaleList", search);
	}

	@Override
	public void updatePurchase(Purchase purchase) {
		sqlSession.update("PurchaseMapper.updatePurchase", purchase);
	}

	@Override
	public void updateTranCode(Purchase purchase) {
		sqlSession.update("PurchaseMapper.updateTranCode", purchase);
	}

	@Override
	public int getTotalCountForPurchaseList(Search search) throws Exception {
		return sqlSession.selectOne("PurchaseMapper.getTotalCountForPurchaseList", search);
	}

	@Override
	public int getTotalCountForSaleList(Search search) throws Exception {
		return sqlSession.selectOne("PurchaseMapper.getTotalCountForSaleList", search);
	}

}
