package pl.luxoft.qpr.bilykov.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import pl.luxoft.qpr.bilykov.exception.IncomeCategoryServiceException;
import pl.luxoft.qpr.bilykov.repository.IncomeCategoryRepository;
import pl.luxoft.qpr.bilykov.repository.UserRepository;
import pl.luxoft.qpr.bilykov.dto.IncomeCategoryRequest;
import pl.luxoft.qpr.bilykov.exception.IncomeCategoryServiceException;
import pl.luxoft.qpr.bilykov.model.IncomeCategory;
import pl.luxoft.qpr.bilykov.repository.IncomeCategoryRepository;
import pl.luxoft.qpr.bilykov.repository.UserRepository;
import pl.luxoft.qpr.bilykov.service.IncomeCategoryService;

import java.util.List;

/**
 * @author Bondar Dmytro.
 */
@Component
public class IncomeCategoryServiceDefault implements IncomeCategoryService {

    @Autowired
    IncomeCategoryRepository categoryRepository;

    @Autowired
    UserRepository userRepository;

    public List<IncomeCategory> getCategories() {
        return categoryRepository.findAll();
    }

    public IncomeCategory getCategory(Integer id) {
        return categoryRepository.findOne(id);
    }

    public void add(IncomeCategoryRequest request) {
        IncomeCategory category = new IncomeCategory();
        category.setUser(userRepository.findOne(request.getUser()));
        if (request.getParentId() != null) {
            IncomeCategory parent = categoryRepository.findOne(request.getParentId());
            if (parent.getParentId() != null)
                throw new IncomeCategoryServiceException("Can't add a subcategory for subcategory");
            category.setParentId(parent);
        } else category.setParentId(null);
        category.setName(request.getName());
        categoryRepository.save(category);
    }

    public void delete(Integer id) {
        if (categoryRepository.findOne(id) == null)
            throw new IncomeCategoryServiceException("There is no such category");
        categoryRepository.delete(id);
    }

    public void changeName(Integer id, String newName) {
        if (categoryRepository.findOne(id) == null)
            throw new IncomeCategoryServiceException("There is no such category");
        IncomeCategory category = categoryRepository.findOne(id);
        category.setName(newName);
    }

    public void changeParent(Integer id, Integer newParent) {
        if (categoryRepository.findOne(id) == null)
            throw new IncomeCategoryServiceException("There is no such category");
        IncomeCategory category = categoryRepository.findOne(id);
        IncomeCategory parent = null;
        if (newParent != null) {
            parent = categoryRepository.findOne(newParent);
            if (parent == null)
                throw new IncomeCategoryServiceException("There is no such parent category");
        }
        category.setParentId(parent);
    }

    public List<IncomeCategory> getUsersCategory(Integer id) {
        return categoryRepository.findByUserUserId(id);
    }

    public List<IncomeCategory> getChildesCategory(Integer id) {
        return categoryRepository.findByParentIdCategoryId(id);
    }
}
