import { Routes } from '@angular/router';
import { LayoutComponent } from './shared/components/layout/layout.component';
import { authGuard } from './core/guards/auth.guard';
import { roleGuard } from './core/guards/role.guard';

export const routes: Routes = [
  // público
  {
    path: 'login',
    loadComponent: () =>
      import('./features/auth/login/login.component').then((m) => m.LoginComponent),
  },

  // privado
  {
    path: '',
    component: LayoutComponent,
    canActivate: [authGuard],
    children: [
      // HOME (todos)
      {
        path: '',
        pathMatch: 'full',
        redirectTo: 'home',
      },
      {
        path: 'home',
        loadComponent: () =>
          import('./features/home/home.component').then((m) => m.HomeComponent),
      },

      // DASHBOARD (ADMIN/SECRETARIA) -> criaremos o component depois
      {
        path: 'dashboard',
        canActivate: [roleGuard(['ADMIN', 'SECRETARIA'])],
        loadComponent: () =>
          import('./features/dashboard/dashboard.component').then((m) => m.DashboardComponent),
      },

      // USERS (ADMIN) -> se você já tiver essa feature, mantém. Se não tiver, remove por enquanto.
      {
        path: 'usuarios',
        canActivate: [roleGuard(['ADMIN'])],
        loadComponent: () =>
          import('./features/usuarios/usuarios-list/usuarios-list.component').then(
            (m) => m.UsuariosListComponent
          ),
      },

      // restantes (cada um pode ter role depois; por enquanto deixa com authGuard)
      {
        path: 'servicos',
        loadComponent: () =>
          import('./features/servicos/servico-list/servico-list.component').then(
            (m) => m.ServicoListComponent
          ),
      },
      {
        path: 'clientes',
        loadComponent: () =>
          import('./features/clientes/cliente-list/cliente-list.component').then(
            (m) => m.ClienteListComponent
          ),
      },
      {
        path: 'veiculos',
        loadComponent: () =>
          import('./features/veiculos/veiculo-list/veiculo-list.component').then(
            (m) => m.VeiculoListComponent
          ),
      },
      {
        path: 'ordens-servico',
        loadComponent: () =>
          import('./features/ordens-servico/os-list/os-list.component').then(
            (m) => m.OsListComponent
          ),
      },
      {
        path: 'itens-servico',
        loadComponent: () =>
          import('./features/itens-servico/item-servico-list/item-servico-list.component').then(
            (m) => m.ItemServicoListComponent
          ),
      },
    ],
  },

  { path: '**', redirectTo: '' },
];
