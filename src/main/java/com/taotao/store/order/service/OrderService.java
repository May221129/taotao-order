package com.taotao.store.order.service;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.taotao.store.order.mapper.OrderMapper;
import com.taotao.store.order.pojo.Order;
import com.taotao.store.order.pojo.OrderItem;
import com.taotao.store.order.pojo.PageResult;
import com.taotao.store.order.pojo.TaotaoResult;
import com.taotao.store.order.util.ValidateUtil;

/**
 * 订单service
 */
@Service
public class OrderService {

	@Autowired
	private OrderMapper orderMapper;

	/**
	 * 创建订单（同时创建订单的商品快照和收货地址）
	 * 
	 * @param order
	 * @param orderItem
	 * @param orderShipping
	 * @return 订单号
	 */
	public TaotaoResult create(Order order) {

		try {
			// 校验order对象：
			ValidateUtil.validate(order);
		} catch (Exception e) {
			e.printStackTrace();
			TaotaoResult.build(400, "请求参数有误！");
		}

		// 初始化order对象：
		/**
		 * 1.生成订单ID，规则为：userid+当前时间戳. 2.订单要符合三点：1.唯一性；2.可读性强；3.不能太长。
		 * 3.下面三种生成orderId的写法有什么不同： 首先要明白，java的执行顺序是从左往右执行，再赋值给等号左边的orderId的。
		 * 其次：String类型和其他Integer或Long等整型相加，最终结果是字符串型，而非进行运算。
		 * 第①种：“order.getUserId() + ""
		 * ”表示得到long类型的userId后将它变成字符串类型，再加上long类型的时间戳，最终结果依旧是个字符串类型。
		 * 第②种：long类型的userId加上long类型的时间戳，得到的结果通过+""变成字符串。这种写法用在这里是错误的，因为最终的结算结果有可能重复。
		 * 第③种：效果和第①种相同，但是写法上面更好。
		 */
		// String orderId = order.getUserId() + "" +
		// System.currentTimeMillis();//①
		// String orderId = order.getUserId() + System.currentTimeMillis() +
		// "";//②
		long now = System.currentTimeMillis();
		String orderId = String.valueOf(order.getUserId()) + now;// ③
		order.setOrderId(orderId);
		// 设置订单的初始状态为未付款:
		order.setStatus(1);
		// 设置订单的创建时间:
		Date date = new Date();
		date.setTime(now);// 将long型的new转为Date类型
		order.setCreateTime(date);
		order.setUpdateTime(order.getCreateTime());
		// 设置买家评价状态，初始为未评价:
		order.setBuyerRate(0);

		// 可以在这里为orderItem的orderId属性赋值，也可以在SQL映射文件中具体实现inserOrder的sql语句中为其赋值。
		for (OrderItem orderItem : order.getOrderItems()) {
			orderItem.setOrderId(orderId);
		}

		// //可以在这里为orderShipping的orderId属性、created、updated赋值，也可在SQL映射文件中具体实现inserOrder的sql语句中为其赋值。
		order.getOrderShipping().setOrderId(orderId);
		order.getOrderShipping().setCreated(order.getCreateTime());
		order.getOrderShipping().setUpdated(order.getOrderShipping().getCreated());

		// 新增订单：
		this.orderMapper.insertOrder(order);

		return TaotaoResult.build(200, "OK", orderId);
	}

	/**
	 * 根据订单ID查询订单(查询结果包括：订单、商品快照、物流地址信息)
	 */
	public Order queryOrderByOrderId(String orderId) {
		// 查询订单，返回order对象：
		return this.orderMapper.selectOrderByOrderId(orderId);
	}
	
	/**
	 * 根据用户昵称分页查询订单。
	 * 返回的结果最好是按照创建时间倒序排序。
	 */
	public PageResult<Order> selectOrderByBuyerNick(String buyerNick, Integer page, Integer rows) {
		PageHelper.startPage(page, rows);
		List<Order> orders = this.orderMapper.selectOrderByBuyerNick(buyerNick);
		PageInfo<Order> pageInfo = new PageInfo<>(orders);
		Integer total = (int) pageInfo.getTotal();
		return new PageResult<>(total, pageInfo.getList());
	}

	/**
	 * 修改订单状态（更改付款状态、时间）
	 */
	public Boolean changeOrderStatus(Order order) {
		// 修改订单更新时间：
		order.setUpdateTime(new Date());
		int count = this.orderMapper.updateOrderByOrderId(order);
		return count != 0;
	}
}
