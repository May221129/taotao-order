package com.taotao.store.order.mapper;

import java.util.List;

import com.taotao.store.order.pojo.Order;
import com.taotao.store.order.pojo.OrderShipping;

/**
 * 1.提问：这里的OrderMapper为什么是接口，而非具体的实现类？
 * 	回答：原因有两点：
 * 		① 因为我用的是MyBatis来实现数据的持久化，这符合MyBatis的使用规范；
 * 		② 因为接口可以有多种实现方式，将来数据量大了，不用MySQL改用其他数据库了，就可以重写接口的实现类，而不改变实际调用接口中的方法的代码。
 * 		当然，在我现在的代码中，如果要换数据库，我只需要改变和数据库相关的配置文件，及该接口的SQL映射文件（/taotao-order/src/main/
 * 		resources/mybatis/mappers/myOrderMapper.xml）的实现即可。
 * 2.和crud的返回值相关：
 * 	新增：一般不需要有返回值，成功了就是成功了，失败了就抛异常了。如果需要返回值，可以返回id.
 * 	更新：int类型的返回值，更新成功的数量。尤其是做批量更新的时候。
 * 	删除：一般不需要有返回值。批量删除一般返回删除条数int.
 * 	查询：肯定有返回值。
 */
public interface OrderMapper {
	
	/**
	 * 新增订单（同时也会新增商品快照）
	 */
	public void insertOrder(Order order);
	
	/**
	 * 根据orderId删除订单（同时删除商品快照和物流地址信息）
	 */
	public int deleteOrderByOrderId(String orderId);
	
	/**
	 * 根据orderId查询订单（含订单详情、商品快照、物流地址信息）
	 */
	public Order selectOrderByOrderId(String orderId);
	
	/**
	 * 根据用户昵称分页查询订单
	 * @param buyerNick
	 * @return List<Order>
	 */
	public List<Order> selectOrderByBuyerNick(String buyerNick);

	/**
	 * 修改订单状态（更改付款状态、时间）
	 * 提问：凭什么确定返回值int就是更新成功的条数？
	 * 答：默认情况下，mybatis 的 update 操作返回值是记录的 matched 的条数，并不是影响的记录条数。
	 *    严格意义上来讲，这并不是 mybatis 的返回值，mybatis 仅仅只是返回的数据库连接驱动（通常是 JDBC ）的返回值，
	 *    也就是说，如果驱动告知更新 2 条记录受影响，那么我们将得到 mybatis 的返回值就会是 2 和 mybatis 本身是没有关系的。
	 * 提问：那么，如何让 mybatis 的 update 操作明确的返回受影响的记录条数？
	 * 答：对我们的数据库连接配置稍做修改，添加 useAffectedRows 字段：//${jdbc.host}/${jdbc.db}?useAffectedRows=true
	 * @return int,更新成功的条数。
	 */
	public int updateOrderByOrderId(Order order);
	
}
