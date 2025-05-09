describe('Test3', () => {
  it('inviteMember', () => {
    cy.visit('http://localhost:4200/')
    cy.get('#email').type('testMouheb1@test.com')
    cy.get('#password').type('test')
    cy.get('.submit-btn').click()
    cy.get(':nth-child(16) > .project-header > .project-actions > .action-buttons > .btn-invite').click()
    cy.get('#email').type('test2@test.comt')
    cy.get('.btn-submit').click()
    cy.get('.logout-btn').click()

  })
})