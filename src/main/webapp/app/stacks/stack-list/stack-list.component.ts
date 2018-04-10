import { Component, EventEmitter, Input, OnChanges, OnInit, Output, SimpleChanges } from '@angular/core';
import { Stack } from '../stack.model';
import { FormControl } from '@angular/forms';
import { Filters } from '../../shared/filters';

@Component({
    selector: 'jhi-stack-list',
    templateUrl: './stack-list.component.html',
    styleUrls: ['./stack-list.component.scss']
})
export class StackListComponent implements OnInit, OnChanges {
    @Input() stacks: Stack[];
    @Output() select = new EventEmitter<Stack>();
    filteredStacks;
    templates;
    owners;
    names;
    selectedTemplates = new FormControl([]);
    selectedNames = new FormControl([]);
    selectedOwners = new FormControl([]);
    constructor() { }

    ngOnInit() {
        this.selectedNames.valueChanges.subscribe(() => this.runFilters());
        this.selectedOwners.valueChanges.subscribe(() => this.runFilters());
        this.selectedTemplates.valueChanges.subscribe(() => this.runFilters());
    }

    ngOnChanges(changes: SimpleChanges): void {
        if (this.stacks) {
            this.templates = this.stacks.map((stack) => stack.template.title).filter(Filters.distinct);
            this.names = this.stacks.map((stack) => stack.name).filter(Filters.distinct);
            this.owners = this.stacks.map((stack) => stack.owner).filter(Filters.distinct);
            this.runFilters();
        }
    }

    selectStack(stack: Stack) {
        this.select.emit(stack);
    }

    private runFilters() {
        if (this.stacks) {
            this.filteredStacks = this.stacks
                .filter((stack) => this.filterTemplates(stack))
                .filter((stack) => this.filterNames(stack))
                .filter((stack) => this.filterOwners(stack));
        }
    }

    private filterNames(stack: Stack) {
        if (this.selectedNames.value.length === 0) {
            return true;
        }
        return this.selectedNames.value.includes(stack.name);
    }

    private filterTemplates(stack: Stack) {
        if (this.selectedTemplates.value.length === 0) {
            return true;
        }
        return this.selectedTemplates.value.includes(stack.template.title);
    }

    private filterOwners(stack: Stack) {
        if (this.selectedOwners.value.length === 0) {
            return true;
        }
        return this.selectedOwners.value.includes(stack.owner);
    }
}
