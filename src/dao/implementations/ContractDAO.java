package dao.implementations;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.ejb.Stateless;
import javax.persistence.Query;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.primefaces.model.SortOrder;

import model.Contract;
import model.enums.StatusContract;
import dao.interfaces.ContractDAOInterface;

@Stateless
public class ContractDAO extends EntityDAO<Contract> implements ContractDAOInterface<Contract> {

	@Override
	public Class<Contract> getEntityClass() {
		return Contract.class;
	}

	@Override
	 public List<Contract> findRange(int start, int count, String sortField,
             SortOrder sortOrder, Map<String, String> filters)  {
		 CriteriaBuilder cb = em.getCriteriaBuilder();
	        CriteriaQuery<Contract> cq = cb.createQuery(getEntityClass());
	        Root<Contract> rt = cq.from(getEntityClass());
	        cq.select(rt);
	        if (!filters.isEmpty()) {
	            Predicate predicate = cb.conjunction();
	            Iterator<String> it = filters.keySet().iterator();
	            while (it.hasNext()) {
	                String filterField = it.next();
	                String filterValue = filters.get(filterField);
	                if (filterField.equals("status")) {
	                    predicate = cb.and(predicate, cb.equal(rt.get(filterField), StatusContract.valueOf(filterValue.toUpperCase())));
	              /*  } else if (filterField.equals("color")) {
	                    predicate = cb.and(predicate, cb.equal(rt.get(filterField), Color.valueOf(filterValue.toUpperCase())));
	                } else if (filterField.equals("price") || filterField.equals("value")) {
	                    predicate = cb.and(predicate, cb.equal(rt.get(filterField), Double.valueOf(filterValue)));*/
	                } else {
	                    predicate = cb.and(predicate, cb.like(rt.<String>get(filterField), filterValue + "%"));
	                }
	            }
	            cq.where(predicate);
	        }

	        if (sortField != null) {
	            if (sortOrder == SortOrder.ASCENDING) {
	                cq.orderBy(cb.asc(rt.get(sortField)));
	            } else if (sortOrder == SortOrder.DESCENDING) {
	                cq.orderBy(cb.desc(rt.get(sortField)));
	            }
	        }

	        Query q = em.createQuery(cq);
	        q.setMaxResults(count);
	        q.setFirstResult(start);
	        return q.getResultList();
	    }
}
