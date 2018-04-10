import { Routes } from '@angular/router';
import { UserRouteAccessService } from '../shared';
import { StacksComponent } from './stacks.component';

export const stackRoute: Routes = [
    {
        path: 'stacks',
        data: {
            authorities: ['ROLE_USER']
        },
        canActivate: [UserRouteAccessService],
        children: [
            {
                path: '',
                component: StacksComponent,
                data: {
                    pageTitle: 'stacks.home.title'
                }
            },
        ]
    }
];
