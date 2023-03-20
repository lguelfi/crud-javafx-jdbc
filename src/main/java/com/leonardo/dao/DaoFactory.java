package com.leonardo.dao;

import com.leonardo.dao.impl.DepartmentDaoJDBC;
import com.leonardo.dao.impl.SellerDaoJDBC;
import com.leonardo.db.DB;

public class DaoFactory {

    public static SellerDao createSellerDao() {
        return new SellerDaoJDBC(DB.getConnection());
    }

    public static DepartmentDao createDepartmentDao() {
        return new DepartmentDaoJDBC(DB.getConnection());
    }
}
