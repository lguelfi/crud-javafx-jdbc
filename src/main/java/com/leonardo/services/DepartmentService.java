package com.leonardo.services;

import java.util.List;

import com.leonardo.dao.DaoFactory;
import com.leonardo.dao.DepartmentDao;
import com.leonardo.entities.Department;

public class DepartmentService {

    private DepartmentDao departmentDao = DaoFactory.createDepartmentDao();

    public List<Department> findAll(){
        return departmentDao.findAll();
    }

    public void saveOrUpdate(Department department) {
        if (department.getId() == null) {
            departmentDao.insert(department);
        }
        else {
            departmentDao.update(department);
        }
    }

    public void remove(Department department) {
        departmentDao.deleteById(department.getId());
    }
}
