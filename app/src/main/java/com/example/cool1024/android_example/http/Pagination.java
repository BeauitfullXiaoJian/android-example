package com.example.cool1024.android_example.http;

public class Pagination {

    public final static int REFRESH = 0;
    public final static int LOAD_MORE = 1;

    // 数据加载状态
    public boolean loading = false;

    // 当前页面
    private int currentPage = 1;

    // 每页显示的数据量
    public int limit = 10;

    // 数据总数
    public int total = 0;

    // 加载模式
    public int loadModel = LOAD_MORE;

    private int appendIndex = 0;
    private int appendCount = 0;

    // 偏移量，当前已经显示的数据量
    public int offset() {
        return (this.currentPage - 1) * this.limit;
    }

    // 保存更新记录
    public void saveUpdate(int appendIndex, int count) {
        this.appendIndex = appendIndex;
        this.appendCount = count;
    }

    // 当前需要更新的开始位置
    public int updateStart() {
        return offset();
    }

    // 当前需要更新的数据量
    public int updateCount() {
        return this.limit;
    }

    // 最大页数
    private int maxPage() {
        return (int) Math.ceil((double) this.total / this.limit);
    }

    // 判断是否有下一页
    public boolean hasNext() {
        return this.currentPage == 1 || this.currentPage < maxPage();
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
