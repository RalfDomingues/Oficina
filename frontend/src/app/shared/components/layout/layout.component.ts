import { Component, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router, RouterOutlet, RouterLink, RouterLinkActive } from '@angular/router';
import { MatSidenavModule } from '@angular/material/sidenav';
import { MatToolbarModule } from '@angular/material/toolbar';
import { MatListModule } from '@angular/material/list';
import { MatButtonModule } from '@angular/material/button';

import { AuthService, Perfil } from '../../../core/auth/auth.service';

type MenuItem = {
  label: string;
  route: string;
  roles?: Perfil[]; // se vazio/undefined -> qualquer logado
};

@Component({
  selector: 'app-layout',
  standalone: true,
  imports: [
    CommonModule,
    RouterOutlet,
    RouterLink,
    RouterLinkActive,
    MatSidenavModule,
    MatToolbarModule,
    MatListModule,
    MatButtonModule,
  ],
  templateUrl: './layout.component.html',
  styleUrl: './layout.component.scss',
})
export class LayoutComponent {
  auth = inject(AuthService);
  private router = inject(Router);

  user = this.auth.getUser();
  perfil = this.auth.getPerfil();


  menu: MenuItem[] = [
    { label: 'Home', route: '/home' },

    { label: 'Dashboard', route: '/dashboard', roles: ['ADMIN', 'SECRETARIA'] },

    { label: 'Ordens de Serviço', route: '/ordens-servico', roles: ['ADMIN', 'SECRETARIA', 'MECANICO'] },

    { label: 'Clientes', route: '/clientes', roles: ['ADMIN', 'SECRETARIA'] },
    { label: 'Veículos', route: '/veiculos', roles: ['ADMIN', 'SECRETARIA'] },

    { label: 'Serviços', route: '/servicos', roles: ['ADMIN', 'SECRETARIA', 'MECANICO'] },

    { label: 'Usuários', route: '/usuarios', roles: ['ADMIN'] },
  ];

  canSee(item: MenuItem): boolean {
    if (!item.roles || item.roles.length === 0) return true;
    return this.auth.hasAnyRole(...item.roles);
  }

  logout(): void {
    this.auth.logout();
    this.router.navigate(['/login']);
  }
}
