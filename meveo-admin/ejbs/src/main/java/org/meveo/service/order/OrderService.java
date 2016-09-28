package org.meveo.service.order;

import java.util.Calendar;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.Query;

import org.meveo.admin.exception.BusinessException;
import org.meveo.model.hierarchy.UserHierarchyLevel;
import org.meveo.model.order.Order;
import org.meveo.model.order.OrderStatusEnum;
import org.meveo.service.base.BusinessService;
import org.meveo.service.hierarchy.impl.UserHierarchyLevelService;

@Stateless
public class OrderService extends BusinessService<Order> {

    @Inject
    private UserHierarchyLevelService userHierarchyLevelService;

	public Long countNewOrders(Calendar endDate) {

        Calendar startDate = Calendar.getInstance();
        startDate.setTime(endDate.getTime());
		startDate.add(Calendar.DATE, -1);

        String sqlQuery = "select count(*) from " + Order.class.getName()
                + " a where a.status = :orderStatus AND a.auditable.created <= :endDate AND a.auditable.created > :startDate";
        Query query = getEntityManager().createQuery(sqlQuery);

		query.setParameter("orderStatus", OrderStatusEnum.ACKNOWLEDGED);
		query.setParameter("endDate", endDate.getTime());
		query.setParameter("startDate", startDate.getTime());
		Long count = (Long) query.getSingleResult();

		return count.longValue();
	}

    public Long countPendingOrders(Calendar startDate) {
		startDate.add(Calendar.DATE, -1);
        Query query = getEntityManager().createQuery("select count(*) from " + Order.class.getName() + " a where a.status = :orderStatus AND a.auditable.created < :startDate");
		query.setParameter("orderStatus", OrderStatusEnum.ACKNOWLEDGED);
		query.setParameter("startDate", startDate.getTime());
		Long count = (Long) query.getSingleResult();

		return count.longValue();
	}

    public Order routeToUserGroup(Order entity, String userGroupCode) throws BusinessException {
        UserHierarchyLevel userHierarchyLevel = userHierarchyLevelService.findByCode(userGroupCode, entity.getProvider()); // Should be a getCurrentUser().getProvider(), but currentUser is not available in non-gui execution environment
        if (userHierarchyLevel == null) {
            log.trace("No UserHierarchyLevel found {}/{}", entity, userGroupCode);
        }
        entity.setRoutedToUserGroup(userHierarchyLevel);
        return this.update(entity, entity.getAuditable().getCreator());
    }
}