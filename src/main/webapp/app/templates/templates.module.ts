import { CUSTOM_ELEMENTS_SCHEMA, NgModule } from '@angular/core';
import { RouterModule } from '@angular/router';
import { TemplatesComponent } from './templates.component';
import { StagerSharedModule } from '../shared/index';
import { DeployComponent } from './deploy/deploy.component';
import { TemplateListComponent } from './template-list/template-list.component';
import { templateRoute } from './templates.route';

/* jhipster-needle-add-entity-module-import - JHipster will add entity modules imports here */

@NgModule({
    imports: [
        StagerSharedModule,
        RouterModule.forChild(templateRoute),
        /* jhipster-needle-add-entity-module - JHipster will add entity modules here */
    ],
    declarations: [
        TemplatesComponent,
        DeployComponent,
        TemplateListComponent
    ],
    entryComponents: [],
    providers: [],
    schemas: [CUSTOM_ELEMENTS_SCHEMA]
})
export class StagerTemplatesModule {}
