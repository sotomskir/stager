export class Filters {
    static distinct(value, index, self) {
        return self.indexOf(value) === index;
    }
}
