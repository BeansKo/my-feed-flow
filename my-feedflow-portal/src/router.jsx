/**
 * 定义应用路
 */
import { HashRouter as Router } from 'react-router-dom';
import { Switch, Route } from 'react-router';
import React from 'react';

import routerConfig from './routerConfig';

/**
 * 将路由信息扁平化，继承上一级路由的 path
 * @param {Array} config 路由配置
 */
function recursiveRouterConfigV4(config = []) {
  const routeMap = [];
  config.forEach((item) => {
    const route = {
      path: item.path,
      layout: item.layout,
      component: item.component,
    };
    if (Array.isArray(item.childRoutes)) {
      route.childRoutes = recursiveRouterConfigV4(item.childRoutes);
    }
    routeMap.push(route);
  });
  return routeMap;
}

/**
 * 将扁平化后的路由信息生成 Route 节点
 *
 * @param {Element} container 路由容器
 * @param {object} router 路由对象
 * @param {string} contextPath 上层路由地址
 * @return {Route}
 * @example
 * <Switch>
 *   <Route exact path="/" component={Home} />
 *   <Route exact path="/page3" component={Page3} />
 *   <Route exact path="/page4" component={Page4} />
 *   <Route exact path="/page3/:id" component={Page3} />
 *   <Route exact component={NotFound} />
 * </Switch>
 */
function renderRouterConfigV4(routers, contextPath) {
  const routeChildren = [];
  const renderRoute = (item, routeContextPath) => {
    let routePath;
    if (item.path == '/' || item.path == '*') {
      routePath = item.path;
    } else {
      routePath = `/${routeContextPath}/${item.path}`.replace(/\/+/g, '/');
    }
    routeChildren.push( <Route key={routePath} exact path={routePath} render={(props) => { 
      return React.createElement(item.layout,props,React.createElement(item.component, props)) 
    }}/> );
    if (Array.isArray(item.childRoutes)) {
      item.childRoutes.forEach((item) => {
        renderRoute(item, routePath);
      });
    }
  };
  routers.forEach((route) => {
    renderRoute(route, contextPath);
  });
  return <Switch>{routeChildren}</Switch>;
}

const routerWithReactRouter4 = recursiveRouterConfigV4(routerConfig);
const routeChildren = renderRouterConfigV4(routerWithReactRouter4, '/');
export default <Router>{routeChildren}</Router>;
