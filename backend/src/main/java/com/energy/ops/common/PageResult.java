package com.energy.ops.common;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 分页返回结构
 *
 * <p>MyBatis-Plus 的 IPage 带了很多分页内部字段，不直接返回给前端；
 * 这里只取前端真正需要的四个：总数、当前页数据、页码、每页条数。
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PageResult<T> {

    /** 总记录数，前端用它算总页数 */
    private long total;

    /** 当前页的数据 */
    private List<T> list;

    /** 当前页码，从 1 开始 */
    private long page;

    /** 每页条数 */
    private long size;
}
