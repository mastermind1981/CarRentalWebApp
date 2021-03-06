package dao.implementations;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import javax.ejb.Stateless;
import javax.persistence.Query;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.ParameterExpression;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import model.Car;
import model.Contract;
import model.enums.CarType;

import org.primefaces.model.SortOrder;

import dao.interfaces.CarDAOInterface;

@Stateless
public class CarDAO extends EntityDAO<Car> implements CarDAOInterface<Car> {

	@Override
	public Class<Car> getEntityClass() {
		return Car.class;
	}

	public long count() {
		Query query = em.createNamedQuery("Car.count");
		return (Long) query.getSingleResult();
	}
	
	
	

	@Override
	public List<Car> findRange(int start, int count, String sortField,
			SortOrder sortOrder, Map<String, String> filters) {
		System.out.println("In List<Car> findRange CArDAO ");
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Car> cq = cb.createQuery(getEntityClass());
		Root<Car> rt = cq.from(getEntityClass());
		cq.select(rt);
		
		if (!filters.isEmpty()) {
			Predicate predicate = cb.conjunction();
			Iterator<String> it = filters.keySet().iterator();
			while (it.hasNext()) {
				String filterField = it.next();
				String filterValue = filters.get(filterField);
				System.out.println("In find Range CarDAO filterField "
						+ filterField + "; filterValue : " + filterValue);
				
				if (filterField.equals("carType")) {
					predicate = cb
							.and(predicate, cb.equal(rt.get(filterField),
									CarType.valueOf(filterValue.toUpperCase())));
				} else if (filterField.equals("carPrice")) {
					System.out.println("In find Range CarDAO price else  "
							+ filterField + "; filterValue : " + filterValue);
					BigDecimal myBigD = new BigDecimal(filterValue);
					BigDecimal error = new BigDecimal("0.001");
					Path<BigDecimal> value = rt.<BigDecimal> get("carPrice");
					predicate = cb.and(
							predicate,
							cb.between(value, myBigD.subtract(error),
									myBigD.add(error)));
				} else if (filterField.equals("carYear")) {
					System.out.println("In find Range CarDAO carYear else  "
							+ filterField + "; filterValue : " + filterValue);
					Integer candidate = Integer.parseInt(filterValue);
					Expression<Integer> year = cb.function("year",
							Integer.class, rt.get(filterField));
					predicate = cb.and(predicate, cb.equal(year, candidate));
				} else {
					predicate = cb.and(
							predicate,
							cb.like(rt.<String> get(filterField), "%"
									+ filterValue + "%"));
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
	
	
	
	
	public int count(Map<String, String> filters) {
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery cq = cb.createQuery(getEntityClass());
		Root<Car> rt = cq.from(getEntityClass());
		cq.select(cb.count(rt));
		if (!filters.isEmpty()) {
			Predicate predicate = cb.conjunction();
			Iterator<String> it = filters.keySet().iterator();
			while (it.hasNext()) {
				String filterField = it.next();
				String filterValue = filters.get(filterField);
				if (filterField.equals("carType")) {
					predicate = cb
							.and(predicate, cb.equal(rt.get(filterField),
									CarType.valueOf(filterValue.toUpperCase())));
				} else if (filterField.equals("carPrice")) {
					BigDecimal myBigD = new BigDecimal(filterValue);
					predicate = cb.and(predicate,
							cb.equal(rt.get(filterField), myBigD));
				} else if (filterField.equals("carYear")) {
					System.out.println("In count Range CarDAO carYear else  "
							+ filterField + "; filterValue : " + filterValue);
					Integer candidate = Integer.parseInt(filterValue);
					Expression<Integer> year = cb.function("year",
							Integer.class, rt.get(filterField));
					predicate = cb.and(predicate, cb.equal(year, candidate));

				} else {
					predicate = cb.and(
							predicate,
							cb.like(rt.<String> get(filterField), filterValue
									+ "%"));
				}
			}
			cq.where(predicate);
		}
		Query q = em.createQuery(cq);
		return ((Long) q.getSingleResult()).intValue();
	}

	@Override
	public List<Car> findAvailableBeetwenDates(Date contractDateFrom, Date c) {
		String dateFrom = new SimpleDateFormat("dd/MM/YYYY")
				.format(contractDateFrom);
		String dateTo = new SimpleDateFormat("dd/MM/YYYY").format(c);
	/*	System.out.println("findAvailableBeetwenDates DateFrom" + dateFrom);
		System.out.println("findAvailableBeetwenDates DateTo" + dateTo);*/
		Query query = em.createNamedQuery("Car.findAvailableBeetwenDates",
				Car.class);
		query.setParameter("from", dateFrom);
		query.setParameter("to", dateTo);
		
		
		
		List<Car> result = query.getResultList();
		System.out.println(result.size());
		return result;
	}

	@Override
	public List<Car> findRange(int first, int pageSize, String sortField,
			SortOrder sortOrder, Map<String, String> filters,
			String contractDateFrom, String contractDateTo) {
	/*	CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Car> cq = cb.createQuery(getEntityClass());
		Root<Car> rt = cq.from(getEntityClass());
		cq.select(rt);

		if (!filters.isEmpty()) {
			Predicate predicate = cb.conjunction();
			Iterator<String> it = filters.keySet().iterator();
			while (it.hasNext()) {
				String filterField = it.next();
				String filterValue = filters.get(filterField);
				System.out.println("In find Range CarDAO filterField "
						+ filterField + "; filterValue : " + filterValue);
				predicate = cb.and(
							predicate,
							cb.like(rt.<String> get(filterField), "%"
									+ filterValue + "%"));
				
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
		return q.getResultList();*/
		return null;
	}

}
