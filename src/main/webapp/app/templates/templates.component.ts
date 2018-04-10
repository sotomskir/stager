import { Component, OnDestroy, OnInit } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { SERVER_API_URL } from '../app.constants';
import { Template } from './template.model';

@Component({
    selector: 'jhi-templates',
    templateUrl: './templates.component.html'
})
export class TemplatesComponent implements OnInit, OnDestroy {
    stacks: Template[];
    containers: Template[];
    selectedTemplate: Template;
    tabs = [
        {code: 'stack', label: 'Stacks'},
        {code: 'container', label: 'Containers'},
    ];
    activeTab = this.tabs[0];

    constructor(private http: HttpClient) {
    }

    ngOnInit(): void {
        this.http.get<Template[]>(SERVER_API_URL + 'api/templates')
            .subscribe((templates) => {
                this.containers = templates.filter((stack) => stack.type === 'container');
                this.stacks = templates.filter((stack) => stack.type === 'stack');
                console.log(this.containers);
                console.log(this.stacks);
        });
    }

    ngOnDestroy(): void {
    }

    selectTemplate(template: Template) {
        this.selectedTemplate = template;
    }

    clearSelectedTemplate() {
        this.selectedTemplate = null;
    }

    activateTab(tab) {
        this.activeTab = tab;
    }

    isActive(tab) {
        return this.activeTab.code === tab.code;
    }
}
