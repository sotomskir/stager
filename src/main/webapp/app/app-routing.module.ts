import { NgModule } from '@angular/core';
import { RouterModule } from '@angular/router';
import { errorRoute, NavbarComponent } from './layouts';
import { DEBUG_INFO_ENABLED } from './app.constants';

const MAIN_ROUTES = [
    {
        path: '',
        component: NavbarComponent,
        outlet: 'navbar'
    },
    ...errorRoute
];

@NgModule({
    imports: [
        RouterModule.forRoot(MAIN_ROUTES, { useHash: true , enableTracing: DEBUG_INFO_ENABLED })
    ],
    exports: [
        RouterModule
    ]
})
export class StagerAppRoutingModule {}
