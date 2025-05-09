describe('Test2', () => {
  it('cerateProject', () => {
    cy.visit('http://localhost:4200/')
    cy.get('#email').type('testMouheb1@test.com')
    cy.get('#password').type('test')
    cy.get('.submit-btn').click()
    cy.get('h1').should('contain', 'Tableau de bord')
    cy.get(':nth-child(1) > .nav-link').click()
    cy.get('#name').type('projectMouheb1')
    cy.get('#description').type('projectMouheb1') 
    cy.get('#startDate').type('2025-01-01')
    cy.get('.btn-submit').click()
  })
})