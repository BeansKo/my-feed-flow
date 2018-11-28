// 菜单配置
// headerMenuConfig：头部导航配置
// asideMenuConfig：侧边导航配置

const headerMenuConfig = [
  {
    name: '首页',
    path: '/',
    icon: 'home',
  },
  {
    name: '反馈',
    path: 'https://github.com/alibaba/ice',
    external: true,
    newWindow: true,
    icon: 'message',
  },
  {
    name: '帮助',
    path: 'https://alibaba.github.io/ice',
    external: true,
    newWindow: true,
    icon: 'bangzhu',
  },
];

const asideMenuConfig = [
  {
    name: '概览',
    path: '/',
    icon: 'home2',
  },
  {
    name: 'JOB',
    path: '/job',
    icon: 'copy',
  },
  {
    name: 'Type',
    path: '/jobtype',
    icon: 'cascades',
  },
  {
    name: 'Email',
    path: '/email',
    icon: 'person',
  },
  {
    name: 'DB',
    path: '/database',
    icon: 'shezhi',
  },
];

export { headerMenuConfig, asideMenuConfig };
