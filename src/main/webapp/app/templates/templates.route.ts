import { Routes } from '@angular/router';
import { TemplatesComponent } from './templates.component';
import { DeployComponent } from './deploy/deploy.component';
import { UserRouteAccessService } from '../shared';

export const templateRoute: Routes = [
    {
        path: 'templates',
        data: {
            authorities: ['ROLE_USER']
        },
        canActivate: [UserRouteAccessService],
        children: [
            {
                path: '',
                component: TemplatesComponent,
                data: {
                    pageTitle: 'templates.home.title'
                }
            },
            {
                path: ':name/deploy',
                component: DeployComponent,
                data: {
                    pageTitle: 'templates.home.title'
                }
            }
        ]
    }
];
