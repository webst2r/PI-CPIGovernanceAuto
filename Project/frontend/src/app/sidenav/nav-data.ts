export const navbarData = [
  {
    routeLink: 'login',
    icon: 'fa fa-sign-in',
    label: 'side_nav.login',
    authRequired: false,
  },
  {
    routeLink: 'home',
    icon: 'fal fa-home',
    label: 'side_nav.home',
    authRequired: true
  },
  {
    routeLink: 'packages',
    icon: 'fas fa-folder-open',
    label: 'side_nav.packages',
    authRequired: true
  },
  {
    routeLink: 'credentials',
    icon: 'fa fa-key',
    label: 'side_nav.credentials',
    authRequired: true
  },
  {
    routeLink: 'logout',
    icon: 'fas fa-sign-out-alt',
    label: 'side_nav.logout',
    authRequired: true
  }
];
