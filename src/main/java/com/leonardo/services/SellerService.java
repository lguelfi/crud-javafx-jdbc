package com.leonardo.services;

import java.util.List;

import com.leonardo.dao.DaoFactory;
import com.leonardo.dao.SellerDao;
import com.leonardo.entities.Seller;

public class SellerService {

    private SellerDao sellerDao = DaoFactory.createSellerDao();

    public List<Seller> findAll(){
        return sellerDao.findAll();
    }

    public void saveOrUpdate(Seller seller) {
        if (seller.getId() == null) {
            sellerDao.insert(seller);
        }
        else {
            sellerDao.update(seller);
        }
    }

    public void remove(Seller seller) {
        sellerDao.deleteById(seller.getId());
    }
}
