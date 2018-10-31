package com.example.cool1024.android_example.http;

public class Pagination {

    // 数据加载状态
    public boolean loading = false;

    // 当前页面
    public int currentPage = 1;

    // 每页显示的数据量
    public int limit = 10;

    // 数据总数
    public int total = 0;

    // 偏移量，当前已经显示的数据量
    public int offset() {
        return (this.currentPage - 1) * this.limit;
    }

    // 最大页数
    public int maxPage() {
        return (int) Math.ceil((double) this.total / this.limit);
    }

    // 判断是否有下一页
    public boolean hasNext() {
        return this.currentPage < maxPage();
    }

    // 判断是否有上一页
    public boolean hasPre() {
        return this.currentPage > 1;
    }

    // 重置分页
    public void reset() {
        this.currentPage = 1;
        this.total = 0;
        this.limit = 10;
        this.loading = false;
    }

    // 下一页
    public void nextPage() {
        if (hasNext()) {
            currentPage++;
        }
    }

    // 上一页
    public void prePage() {
        if (hasPre()) {
            currentPage--;
        }
    }

    // 获取分页参数,可以附带额外参数
    public void params() {

    }

    // 转化为queryParams串
    public String toQueryString() {
        return "limit=" + limit + "&offset=" + offset();
    }

    // 设为繁忙状态
    public void setLoading() {
        this.loading = true;
    }

    // 设置为空闲状态
    public void setComplete() {
        this.loading = false;
    }
}
