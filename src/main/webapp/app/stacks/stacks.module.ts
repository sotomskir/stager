import { CUSTOM_ELEMENTS_SCHEMA, NgModule } from '@angular/core';
import { RouterModule } from '@angular/router';
import { StagerSharedModule } from '../shared/index';
import { stackRoute } from './stacks.route';
import { StackListComponent } from './stack-list/stack-list.component';
import { StacksComponent } from './stacks.component';
import { ReactiveFormsModule } from '@angular/forms';
import { MatSelectModule } from '@angular/material';

/* jhipster-needle-add-entity-module-import - JHipster will add entity modules imports here */

@NgModule({
    imports: [
        StagerSharedModule,
        StagerSharedModule,
        RouterModule.forChild(stackRoute),
        /* jhipster-needle-add-entity-module - JHipster will add entity modules here */
    ],
    declarations: [
        StacksComponent,
        StackListComponent
    ],
    entryComponents: [],
    providers: [],
    schemas: [CUSTOM_ELEMENTS_SCHEMA]
})
export class StagerStacksModule {}
