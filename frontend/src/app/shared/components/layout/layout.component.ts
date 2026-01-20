import { Component, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router, RouterOutlet, RouterLink, RouterLinkActive } from '@angular/router';
import { MatSidenavModule } from '@angular/material/sidenav';
import { MatToolbarModule } from '@angular/material/toolbar';
import { MatListModule } from '@angular/material/list';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatTooltipModule } from '@angular/material/tooltip';

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
    MatIconModule,
    MatTooltipModule,
  ],
  templateUrl: './layout.component.html',
  styleUrl: './layout.component.scss',
})
export class LayoutComponent {
  private readonly auth = inject(AuthService);
  private readonly router = inject(Router);

  /** Snapshot do usuário/perfil no momento de criação do layout. */
  readonly user = this.auth.getUser();
  readonly perfil = this.auth.getPerfil();

  /**
   * Menu principal. Itens com `roles` restringem visibilidade por perfil.
   * Se `roles` não existir, qualquer usuário logado pode ver.
   */
  readonly menu: MenuItem[] = [
    { label: 'Home', route: '/home' },
    { label: 'Dashboard', route: '/dashboard', roles: ['ADMIN', 'SECRETARIA'] },
    { label: 'Ordens de Serviço', route: '/ordens-servico', roles: ['ADMIN', 'SECRETARIA', 'MECANICO'] },
    { label: 'Clientes', route: '/clientes', roles: ['ADMIN', 'SECRETARIA'] },
    { label: 'Veículos', route: '/veiculos', roles: ['ADMIN', 'SECRETARIA'] },
    { label: 'Serviços', route: '/servicos', roles: ['ADMIN', 'SECRETARIA', 'MECANICO'] },
    { label: 'Itens de Serviço', route: '/itens-servico', roles: ['ADMIN', 'SECRETARIA', 'MECANICO'] },
    { label: 'Usuários', route: '/usuarios', roles: ['ADMIN'] },
  ];

  /** Decide se um item aparece no menu conforme perfis permitidos. */
  canSee(item: MenuItem): boolean {
    if (!item.roles || item.roles.length === 0) return true;
    return this.auth.hasAnyRole(...item.roles);
  }

  /** Encerra sessão e redireciona para login. */
  logout(): void {
    this.auth.logout();
    this.router.navigate(['/login']);
  }
}
