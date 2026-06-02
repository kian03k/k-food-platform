<template>
  <footer>
      <div class="container">
        <div class="row">
            <div class="col-md-7">
                <div>
                    <img src="@/assets/img/logo1.jpeg"  alt="孔家美食网" height="61" width="121">
                    <!--<h1 style="display: inline-block">学成网</h1>-->
                    <!-- <img src="/static/img/asset-logoIco.png" alt=""> -->
                </div>
                <div>菜谱智能推荐系统-为您提供省心且健康的菜谱</div>
                <div>2024年3月28日创建</div>
                <input type="button" class="btn btn-primary" value="公司简介" />
            </div>
            <div class="col-md-5 row">
                <dl class="col-md-4">
                    <dt>关于美食网</dt>
                    <dd>管理团队</dd>
                    <dd>工作机会</dd>
                    <dd>客户服务</dd>
                    <dd>帮助</dd>
                </dl>
                <dl class="col-md-4">
                    <dt>新手指南</dt>
                    <dd>如何注册</dd>
                    <dd>如何收藏</dd>
                    <dd></dd>
                </dl>
                <dl class="col-md-4">
                    <dt>合作伙伴</dt>
                    <dd>合作机构</dd>
                    <dd>合作创作者</dd>
                </dl>
            </div>
        </div>
    </div>
  </footer>
</template>

<script lang="ts">
import { Component, Prop, Vue } from "vue-property-decorator";

@Component({
  name: "PortalFooter"
})
export default class extends Vue {}
</script>

<style lang="scss" scoped>
@import "index";
</style>